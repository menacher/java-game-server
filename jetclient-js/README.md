This is a **Javascript** client project for [jetserver](https://github.com/menacher/java-game-server/tree/master/jetserver) library. An example html which can connect to a locally running Jetserver is located at **test/jetclient.html**.

About the Client
================
Click on `Start War!` button after loading jetclient.html in a websocket compatible browser to start the game. Assumption is that Jetserver is running and jetclient.html is using accurate hostname and port number.

Usage as your own game client
=============================
The general usage steps could be as outlined below.    
1.  Create a `config` object containing the `user`, `password` and `connectionkey` to connect to game room. If you are using a different protocol, then add the appropriate `CodeChain`'s also to the `config` object. By default `JSon` encoding/decoding is used.     
2.  Create a `session`(s) using the `SessionFactory` by passing in a `url` for the remote jetserver, `config` object and a `callback` function which will receive the `session` object after successful login to remote jeteserver.    
3.  Add necessary handlers to the session using `addHandler` function. The default events are provided the `jet` class for e.g `jet.lOG_IN`. At the very least you would want to add a handler for the `jet.SESSION_MESSAGE` event to receive incoming events from jetserver.    
4.  Event objects to be sent to server can be created using the `jet.nevent` function.    
5.  Data can be send to remote server using `session.send` function.    
    
Happy Coding!!
        