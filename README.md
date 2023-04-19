# Five cards poker

This is my first project in Java I realized during my 3rd study semester. <br/>
The goal of project was to implement a 5 cards poker server. <br/>
Whole project is based on java.net Socket communication. <br/>

Enjoy your GAME!

### 1. How to start the program:

    mvn clean install
    java -jar .\SocketServer\target\SocketServer-1.0.0-SNAPSHOT.jar
    java -jar .\SocketClient\target\SocketClient-1.0.0-SNAPSHOT.jar		

### 2. Core information:

- The server can handle any number of games at once.
- The server can send basic information to players from the console.
- The server can check basic information about players.
- Players can send messages to each other in various ways.
- They can create, add to, and exit "decks."
 -Exiting a "deck" is only possible if it has not yet been filled.
- If someone leaves a "deck" during a game, the "deck" is removed and players can join an existing one or create a new one.

### 2. Commands:

Client side:

    \help - print all commands
        \showusers - print all users
        \showdecks - print all running decks
        \adddeck <nameOfDeck> <numberOfPlayers>
        \joindeck <nameOfDeck>
        \leavedeck
        \msgall - msg all connected users
        \<username> - msg specified user
        \info - show in-game info about the current player (if in game)
        \fold - ONLY IN GAME use it if you want to pass
        \check - ONLY IN GAME use it if you want to check a player
        \call - ONLY IN GAME use it if you want to call someone's bet
        \bet <money> - ONLY IN GAME use it if you want to bet
        \raise <money> - ONLY IN GAME use it if you want to re-bet
        \all - ONLY IN GAME use it if you can't call
        \cya - ONLY IN GAME use it after you run out of money
        \exchange <cardNum cardNum> - ONLY IN GAME use it to exchange cards
        \bye - exit

Server site:

	\help - print all commands
        \showusers - print all users
        \showdecks - print all running decks
        \msgall - msg all connected users
        \<username> - msg specified user
        \info - show in-game info about current players (if in game)
        \CLOSE - exit

###  3. Game rules:
- At the beginning of each round, 2 is deducted from each player's account.
- The rules are the same as in standard five-card draw poker.
- If a player runs out of funds in their account, a new game starts with "-" and they must type \cya.
- The program removes the player from the deck, so they can join or create a new game.
- The game ends when there is only one player left.