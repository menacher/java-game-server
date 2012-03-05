This is a client project for [jetserver](https://github.com/menacher/java-game-server/tree/master/jetserver) library. An example main class is provided in src/main/test folder.

About the Client
================
Execute org.menacheri.TestClass from command line or eclipse and it will connect to remote jetserver and start receiving events. Assumption is that TestClass using accurate hostname and port number.
Execution  
---------
Pointers on main classes, classpaths and command line flags.    

**To start the client **    
jetclient can be executed from console using below command.        
java -cp ./jetclient-0.1.jar;./netty-3.3.1.Final.jar  org.menacheri.TestClass  

Usage as game client
====================
The general usage steps could be as outlined below.    
1.  Add jetclient-0.1.jar and netty-3.3.1.Final.jar to your project class path.    
2.  Create LoginBuilder, session and SessionEventHandler as shown in TestClass. example-games project has a ZombieJetClient which shows a better example.    
3.  Use the SessionEventHandler to accept events from remote server as well as to write back events to the server.    
4.  To write back to the remote jetserver create a tcp/udp event using the factory method IEvent event = Events.clientOutUDP(messageBuffer); or Events.clientOutTCP(messageBuffer);    
5.  Now call session.onEvent(event); and this event will be transmitted to the remote jetserver.    

Jar Dependencies
----------------
netty-3.3.1.Final.jar         