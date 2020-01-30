# NU-MOD1_ProjectGO
Final assignment of Nedap University module 1

_Method to make JAR and GUI working kindly adapted from Eline and her coach (with Eline's approval)_

--------------------------------------------------------------------------

Take care: this program  __only works if you have Java 11 installed__. Please install Java 11 if necessary.

To get the program up and running, open a command line terminal.
1. First make a clone of this repository by typing in the following lines in the terminal and hitting enter after each line:

`mkdir goGameDirectory`

`cd goGameDirectory`

`git clone https://github.com/huulie/NU-MOD1_ProjectGO.git`

`cd ProjectGO`

Because of the file size, the .jar files have to be download separately from https://huulie.stackstorage.com/s/LDyxYGfy4zMVk0z (password nedapGO). Place them in the same ProjectGO directory.

2. Then, start the server by typing this line in the command line and hitting enter:

__Mac users:__

`./server`



__Windows users:__

`server.bat`

__Both types of users:__ _Follow the prompts in the terminal._

3. Open a new command line window and navigate to the right directory by typing the following lines in the terminal and hitting enter:


`cd ProjectGO`

Then, start a client by typing  __one of these lines__  in the command line and hitting enter:

__Mac users:__

`./client`			_This will start a client to play on the server._



__Windows users:__

`client.bat`			_This will start a client to play on the server._



__Both types of users:__ _Follow the prompts in the terminal. IP and port number are provided by the server._

4. Repeat step 3, starting another client (maybe on a different computer).

5. Once a client indicates that it has closed the connection, you can type 'ctrl c' to stop the execution and to start another client or server, if wanted.

If wanted, you can add more clients. For every two clients added, a game will be started. Clients on other computers running the same program (or a different program with the same communication protocol) can also connect to your server. Similarly, you can connect as a client to another computer running a server with the same communication protocol. 

enjoy the game, and let’s *GO*!
