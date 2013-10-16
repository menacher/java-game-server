This is a client project for [Nadron](https://github.com/menacher/java-game-server/tree/netty4/nadron) library. An example main class is provided in src/main/test folder.

About the Client
================
Maven
-----
```xml
<dependency>
    <groupId>com.github.menacher</groupId>
    <artifactId>nadclient</artifactId>
    <version>0.5</version>
</dependency>
```
Execution
---------
Execute io.nadron.TestClass from command line or eclipse and it will connect to remote Nadron server and start receiving events. Assumption is that TestClass using accurate hostname and port number.
Execution  
---------
Pointers on main classes, classpaths and command line flags.    

**To start the client **    
client can be executed from console using below command.        
java -cp ./client-0.1.jar;./netty-all-4.0.10.Final.jar  io.nadron.TestClass  

Usage as game client
====================
The general usage steps could be as outlined below.    
1.  Add client-0.2.jar and netty-4.0.0.CR6.jar to your project class path.    
2.  Create LoginBuilder, session and SessionEventHandler as shown in TestClass. example-games project has a ZombieNadClient which shows a better example.    
3.  Use the SessionEventHandler to accept events from remote server as well as to write back events to the server.    
4.  To write back to the remote Nadron server create a tcp/udp network event using the factory method Event event = Events.networkEvent(messageBuffer); or Events.networkEvent(messageBuffer,DeliveryGuaranty.Fast);    
5.  Now call session.onEvent(event); and this event will be transmitted to the remote Nadron server.    

Jar Dependencies
----------------
netty-all-4.0.10.Final.jar         