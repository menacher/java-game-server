**Note** New version of Jetserver is called **Nadron** and is in a new [ netty 4 branch](https://github.com/menacher/java-game-server/tree/netty4) of this same repo.    
JetServer is a java nio based server specifically designed for multiplayer games. It supports UDP and TCP transports. It uses [Netty](http://netty.io/) for high speed network transmission and [Jetlang](http://code.google.com/p/jetlang/ "jetlang") for extremely fast in-vm message passing between player sessions and game rooms. The project also uses spring for its dependency injection. This way, it is highly configurable and you can swap out any part of the server with your own implementations.

## Wiki
The [wiki](https://github.com/menacher/java-game-server/wiki) provides implementation level details and answers to general questions that a developer starting to use jetserver might have about it. The following [blog](http://nerdronix.blogspot.com/2013/06/creating-multiplayer-game-using-html-5.html) has post on game creation.  

## Support Group
For general discussion please use the [jetserver google group](https://groups.google.com/forum/#!forum/jetserver). Issues and bugs can be raised directly in github.

## Installation

Maven
-----
```xml
<dependency>
    <groupId>com.github.menacher</groupId>
    <artifactId>nadron</artifactId>
    <version>0.5</version>
</dependency>
```
From Source
-----------
Using pre-built jar files
-------------------------
The pre-built jar files of this project are located in the jetserver/binaries directory. All dependent jars are located in the jetserver/lib directory. You can add them to your favorite IDE’s classpath for coding. If you want to compile from source, then follow steps below.

With Maven and using Eclipse
----------------------------
**Pre-requisites**: Please have maven 3+ and [Spring source tool suite](http://www.springsource.com/developer/sts "STS") or eclipse installed. If you are using plain vanilla eclipse, then M2Eclipse and EGit plugins though not required will be helpful. The maven-eclipse plugin part of pom.xml should be modified when using another IDE. 

Steps
-----
1.  git clone git@github.com:menacher/java-game-server.git
2.  cd java-game-server
3.  cd jetserver
4.  mvn eclipse:eclipse - **Takes time, the first time!** If you want to reduce this time, then comment out include sources/jars option from the maven pom.xml eclipse plugin part.
5.  eclipse -> file -> import -> git -> select repository and import jetserver project.
6.  jetserver project in eclipse -> right click on pom.xml -> run as -> maven test - **Takes time the first time!**

If everything works as expected you should see some test cases executed successfully!

With Ant
--------
If you are using ant, then the lib folder within the jetserver project contains all the dependent libraries. Just right click and run ant build and it will create the jetserver jar.

*Happy coding!*
