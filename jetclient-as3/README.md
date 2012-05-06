**NOTE**: This client can connect to JetServer but the coding, documenation etc are still work in progress.   
This is a client project for [jetserver](https://github.com/menacher/java-game-server/tree/master/jetserver) library. An example main class is provided in src/main/flex org.menacheri package.

About the Client
================
Execute Main.as from IDE and it will connect to remote jetserver and start receiving events. Assumption is that JetServer is running and Main.as is using accurate hostname and port number.

Usage as game client
====================
The general usage steps could be as outlined below.    
1.  Create a loginHelper.     
2.  Create a session factory using this login helper instance.    
3.  Create as many sessions as required using this factory. Generally only one is required for a client.    
4.  Check the code in Main.as for more information on how to add an event listener to session to receive events.    
5.  Main.as also has code on how to write back to JetServer. Mostly it is done using session.sendToServer(JetEvent).    

        