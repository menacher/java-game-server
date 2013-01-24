This is a javascript client project for [jetserver](https://github.com/menacher/java-game-server/tree/master/jetserver) library. An example html which can connecct to a locally running Jetserver is located at src/test/jetclient.html .

About the Client
================
Click on `start war` button after loading jetclient.html in a websocket enabled browser to start the game. Assumption is that JetServer is running and jetclient.html is using accurate hostname and port number.

Usage as your own game client
=============================
The general usage steps could be as outlined below.    
1.  Create a config object containing the username, password and connectionkey to connect to game room. If you are using a different protocol, then add the appropriate codec chains also to the config object. By default JSon encoding/decoding is used.     
2.  Create a session using the session factory by passing in a url for the remote jetserver, config object and a callback function which will receive the session object after successful login to remote jeteserver.    
3.  Create as many sessions as required using this factory. Generally only one is required for a client. In the example 50 sessions are created.    
4.  Add necessary handlers to the session using `addHandler` function. The default events generated are provided the jet.Events class. At the very least you would want to add a handler for the jet.Events.SESSION_MESSAGE event to receive incoming events from jetserver.    
5.  Data can be send to remote server using `session.send` function.    
    
Happy Coding!!
        