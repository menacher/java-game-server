This project holds some example multi-player games that uses the [nadron](https://github.com/menacher/java-game-server/tree/master/nadron) library. The server part is in src/main/java and the client part of the games are in src/test/java.

Starting the Server
==================
The main class is io.nadron.GameServer in the src/main/java folder. The game clients are coded in the src/test/java folder. The main class for the zombie game is io.nadron.zombie.ZombieClient.
Execution
---------
Pointers on main classes, classpaths and command line flags.    

**To start the game server**    
Set the classpath and provide the log4jconfiguration flag.    
set serverclasspath = ./nadron-0.5.jar;./....    
java -cp $serverclasspath -Dlog4j.configuration=GameServerLog4j.properties io.nadron.GameServer    
**To start the zombie client**    
set clientclasspath = ./nadclient-0.5.jar;./netty-all-4.0.10.Final.jar....    
java -cp clientclasspath io.nadron.ZombieClient   

Jar Dependencies
----------------
The jar dependencies of this project are provided explicitly. Since maven is used these are all down loaded automatically.    
aopalliance-1.0.jar   
backport-util-concurrent-3.1.jar    
blazeds-core-3.2.0.3978.jar    
cglib-nodep-2.1_3.jar    
commons-logging-1.1.1.jar    
jetlang-0.2.9.jar    
nadron-0.5.jar 
nadclient-0.5.jar   
log4j-1.2.16.jar    
netty-all-4.0.10.Final.jar
msgpack-0.6.8.jar
json-simple-1.1.1.jar              
slf4j-api-1.6.1.jar    
slf4j-log4j12-1.6.1.jar    
spring-aop-3.1.0.RELEASE.jar    
spring-asm-3.1.0.RELEASE.jar    
spring-beans-3.1.0.RELEASE.jar    
spring-context-3.1.0.RELEASE.jar    
spring-core-3.1.0.RELEASE.jar    
spring-expression-3.1.0.RELEASE.jar    

## Trouble Shooting

If you get the following property access exception    
    PropertyAccessException 2: org.springframework.beans.MethodInvocationException:    
    Property 'undead' threw exception; nested exception is java.lang.NoSuchMethodError:
    io.nadron.aspect.AppManagedAspect.ajc$if$ac5(Lorg/menacheri/aspect/AppManaged;)Z   
This is mostly because of the eclipse project not having proper binaries compiled to its target. Just goto Project->clean for both the nadron as well as client project **without** doing a maven clean and it should 
work the second time.    
    
If you get the following log4j configuration error, then it is mostly probably because you have not set 
the -Dlog4j.configuration=GameServerLog4j.properties as a flag in your vm path.    
    log4j:ERROR Could not read configuration file [null].
    java.lang.NullPointerException
        at java.io.FileInputStream.<init>(FileInputStream.java:116)    
        at java.io.FileInputStream.<init>(FileInputStream.java:79)    
        at org.apache.log4j.PropertyConfigurator.doConfigure(PropertyConfigurator.java:372)    
        at org.apache.log4j.PropertyConfigurator.configure(PropertyConfigurator.java:403)    
        at io.nadron.GameServer.main(GameServer.java:22)   