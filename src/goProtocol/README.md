# Protocol &quot;go&quot;

**Versie:**  **1.0**  
**Datum**: 2020-01-22  
**Status:** Definitieve versie

**Auteurs:** Joep Eding, Rowena Emaus, Tessa Gerritse, Huub Lievestro, Joris van der Meulen, Thomas Mutter en Eline Verbon.

## Algemene afspraken

1. De protocolberichten worden geïmplementeerd als class in Java, en deze wordt gedeeld via GitHub.
2. De eerste versie (versie 1.0) van het protocol moet altijd ondersteund blijven. Als een client vraagt om een versie die niet ondersteund wordt, dan communiceert die client via versie 1.0 met de server (niet een eventuele andere lagere versie dan de gevraagde versie).
3. Berichten bestaan uit één regel (en eindigen dus met een newline (`\n`))
4. Op ongeldige berichten wordt gereageerd met een foutbericht, de verzender van het oorspronkelijke bericht handelt deze af.
5. De server bepaalt de bordgrootte (tweedimensionaal vierkant bord).
6. De server houdt de actuele staat van het bord bij (deze is leidend).
7. De server is geen deelnemer aan het spel, twee clients spelen tegen elkaar.
8. De standaard komi is 0.5.
9. De speler heeft ongelimiteerde tijd om een zet te doen.
10. Als een speler een invalide zet naar de server verzendt, verliest deze het spel.
11. Als een speler de verbinding verliest of verbreekt, verliest deze het spel.

## Lijst van protocolsymbolen

In dit document kan hiernaar worden verwezen met PROTOCOL._naam_

### Commando's  
Commando's zijn altijd het eerste karakter van een bericht en geven aan welke actie verricht moet worden.
Eventuele verdere informatie die voor de afhandeling van het commando nodig is wordt in datzelfde bericht meegegeven,
begrensd door het symbool dat als delimiter aangewezen is. 

| **Symbool** | **Naam** | **Betekenis / beschrijving** | **Opmerkingen** |
| --- | --- | --- | --- |
| `H` | HANDSHAKE | Handshake | Client kan kleur vragen |
| `G` | GAME | Game (spel start) | Server stuur kleur |
| `T` | TURN | Turn (speler is aan de beurt) | Server stuurt bord mee |
| `M` | MOVE | Move (speler doet een zet) | Client stuur move mee |
| `R` | RESULT | Result (resultaat van een zet) | Wordt gevolg door valid of valid |
| `E` | END | End of game |   |
| `Q` | QUIT | Quit game |   |

### Gereserveerde symbolen
De gereserveerde symbolen vertegenwoordigen bepaalde waarden die van speciaal belang zijn voor (de implementatie
van) GO.

| **Symbool** | **Naam** | **Betekenis / beschrijving** | **Opmerkingen** |
| --- | --- | --- | --- |
| `;` | DELIMITER | delimiter |   |
| `?` | ERROR | Invalid message received (try again) | Met versienummer, optionele vrije string |
| `W` | WHITE | White |   |
| `B` | BLACK | Black |   |
| `U` | UNOCCUPIED | Unoccupied (leeg) |   |
| `P` | PASS | Pass |   |
| `V` | VALID | Valid | Server stuurt bord mee |
| `I` | INVALID | Invalid | Server stuurt foutmelding mee |
| `F` | FINISHED | Reason for end: [F]inished |   |
| `C` | CHEAT | Reason for end: [C]heated |   |
| `D` | DISCONNECT | Reason for end: [D]isconnect other player |   |
| `X` | EXIT | Reason for end: E[X]it by other player |   |

# Vastgestelde variabelen

Van de volgende variabelen is vastgelegd in wat voor format ze gecommuniceerd moeten worden:

`naamClient` is een String, met als maximale lengte 10 (de overgebleven karakters worden afgekapt)

`bord` wordt door server verzonden als String, met volgens een lineaire index ([row-major order](https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Row_and_column_major_order.svg/2000px-Row_and_column_major_order.svg.png)) 
de staat van elk veld. De staat van elk veld wordt vertegenwoordigd door de hiervoor gereserveerde symbolen in `PROTOCOL.`:
- `U` = Unoccupied (leeg)
- `W` = White
- `B` = Black

`move`: De client specificeert het veld waar hij zijn steen wil plaatsen als index van het veld in `bord` (integer, NB: 0 tot boarddimension<sup>2</sup>-1) of `PROTOCOL.PASS` voor passen

`requestedVersion` en `finalVersion` zijn Strings met versie van het protocol (zoals vermeld vooraan dit 
document en op GitHub). Als de requestedVersion aanwezig is, zal de server deze gebruiken. Anders wordt versie 1.0 gebruikt voor de communicatie.

# Communicatieprotocol

## Normaal spelverloop

### Handshake (voor elke client)  
1. Client stuurt naar server:  
`PROTOCOL.HANDSHAKE + PROTOCOL.DELIMITER + requestedVersion + PROTOCOL.DELIMITER + naamClient + PROTOCOL.DELIMITER + requestedColor`  
`requestedVersion` en `naamClient` zijn hierboven beschreven. `requestedColor` is optioneel, indien gegeven moet 
het `PROTOCOL.WHITE` of `PROTOCOL.BLACK` zijn.
2. Server reageert met:   
`PROTOCOL.HANDSHAKE + PROTOCOL.DELIMITER + finalVersion  + PROTOCOL.DELIMITER + message`  
`finalVersion` is hierboven beschreven. `message` is optioneel maar mag een vrij tekstbericht van de server bevatten.  
3. Client wacht op bericht server, zodra server twee verbonden clients heeft start deze het spel
  
### Start Game  
1. De server stuurt aan beide clients:  
`PROTOCOL.GAME + PROTOCOL.DELIMITER + bord + PROTOCOL.DELIMITER + color`  
`bord` is zoals hierboven beschreven. `color` wordt gegeven als `PROTOCOL.WHITE` of `PROTOCOL.BLACK` en bevat de aanduiding
van de kleur die door de server aan de speler is toegewezen. Deze hoeft niet overeen te komen met de gevraagde kleur.
2. Clients wachten op bericht server, server stuurt turn naar speler met kleur zwart
  
### Beurt  
1. Server stuurt bord naar client die aan de beurt is:  
`PROTOCOL.TURN + PROTOCOL.DELIMITER + bord + PROTOCOL.DELIMITER + opponentsLastMove`  
`bord` is zoals hierboven beschreven
`opponentsLastMove`is een integer die een intersectie op het bord vertegenwoordigt, de char 'P' of null voor het geven van de eerste beurt aan de eerste speler.
2. Client stuurt move naar server:  
`PROTOCOL.MOVE + PROTOCOL.DELIMITER + move`  
`move` is zoals hierboven beschreven
NB: deze move moet binnen 60 seconden ontvangen zijn, anders wordt het verwerkt als een invalid move. De trage client krijgt dan dus een 'invalid move' bericht en verliest het spel. Beide spelers krijgen een einde spel bericht met `PROTOCOL.CHEATED` als reden.
3. Bij valide move (kan dus ook een pass zijn), stuurt server:  
`PROTOCOL.RESULT + PROTOCOL.DELIMITER + PROTOCOL.VALID + PROTOCOL.DELIMITER + bord`  
`bord` is zoals hierboven beschreven en bevat een versie van het bord waarop de zet van de speler al verwerkt is. 
Client wacht op verder bericht van de server. NB: dit bericht wordt óók verstuurd na de tweede consecutive pass. Pas daarna wordt een bericht over einde spel gestuurd.
4. Bij invalide move, stuurt server:  
`PROTOCOL.RESULT + PROTOCOL.DELIMITER + PROTOCOL.INVALID + PROTOCOL.DELIMITER + message`  
`message` is optioneel maar kan een String met uitleg bevatten van waarom de gestuurde zet ongeldig is. De client verliest het spel.

### Einde spel.
Het spel eindigt normaliter nadat beide spelers meteen na elkaar passen. Het speel eindigt ook wanneer een van de spelers de verbinding 
verlies/verbreekt of een `Q`uit-bericht stuurt. Bij het verbreken/verliezen van de verbinding wint de overblijvende 
speler automatisch. Het spel eindigt ook wanneer een van beide spelers een ongeldige zet verstuurt, die speler verliest 
onmiddelijk.  
1. Server stuur naar beide clients:  
`PROTOCOL.END + PROTOCOL.DELIMITER + reasonEnd + PROTOCOL.DELIMITER + winner + PROTOCOL.DELIMITER + scoreBlack + PROTOCOL.DELIMITER + scoreWhite`  
`reasonEnd` is een van `PROTOCOL.FINISHED`, `PROTOCOL.CHEATED`, `PROTOCOL.DISCONNECT` of `PROTOCOL.QUIT`; 
`winner` geeft aan welke speler gewonnen heeft (is `PROTOCOL.WHITE` of `PROTOCOL.BLACK`);
`scoreBlack` en `scoreWhite` zijn textuele representaties van een double met precisie tot 1 getal achter de komma (note: vergeet het 0.5 bonuspunt voor wit niet - daardoor is er nooit een gelijkspel);   

## Losse gebeurtenissen / uitzonderingen

### Quit

1. Client stuurt naar server: `PROTOCOL.QUIT`
2. Als de client onderdeel is van een lopend spel, dan stuurt de server naar de andere client: game end, met reden e`X`it

### Verbinding verloren

1. Server verliest verbinding met één van de clients
2. Als de client onderdeel is van een lopend spel, dan stuurt de server naar de andere client: game end, met reden `D`isconnect

# Mogelijke uitbreidingen

- Aanpasbare komi
- Maximale tijdsduur voor een beurt
- Starten nieuw spel met zelfde speler
- Leaderboard

Zie voor meer mogelijke uitbreidingen de GitHub issues.
