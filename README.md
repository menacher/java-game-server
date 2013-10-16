Nadron is a java nio based server specifically designed for mutliplayer games. It supports UDP and TCP transports. It uses [Netty](http://netty.io/) for high speed network transmission and [Jetlang](http://code.google.com/p/jetlang/ "jetlang") for extremely fast in-vm message passing between player sessions and game rooms. The project also uses spring for its dependency injection. This way, it is highly configurable and you can swap out any part of the server with your own implementations.

Wiki
====
The [wiki](https://github.com/menacher/java-game-server/wiki) provides implementation level details and answers to general questions that a developer starting to use Nadron server might have about it. This [blog post](http://nerdronix.blogspot.com/2013/06/creating-multiplayer-game-using-html-5.html) contains a decent tutorial on creating a multiplayer game using this server. 
Support Group
=============
For general discussion please use the [Nadron server google group](https://groups.google.com/forum/#!forum/jetserver). Issues and bugs can be raised directly in github.
Installation
============
Maven
-----
```xml
<dependency>
    <groupId>com.github.menacher</groupId>
    <artifactId>nadron</artifactId>
    <version>0.5</version>
</dependency>
```
Using pre-built jar files
-------------------------
The pre-built jar files of this project are located in the nadron/binaries directory. All dependant jars are located in the nadron/lib directory. You can just add them to your classpath in your favorite IDE and start coding. If you want to compile from source, then follow steps below.

With Maven and using Eclipse
----------------------------
**Pre-requisites**: Please have maven 3+ and [Spring source tool suite](http://www.springsource.com/developer/sts "STS") or eclipse installed. If you are using plain vanilla eclipse, then M2Eclipse and EGit plugins though not required will be helpful. If you are using another IDE then the maven-eclipse plugin part in the pom.xml needs to be modified. 

Steps
-----
1.  git clone git@github.com:menacher/java-game-server.git
2.  cd java-game-server
3.  cd nadron
4.  mvn eclipse:eclipse - **Takes time, the first time!** If you want to reduce this time, then comment out include sources/jars option from the maven pom.xml eclipse plugin part.
5.  eclipse -> file -> import -> git -> select repository and import Nadron project.
6.  Nadron project in eclipse -> right click on pom.xml -> run as -> maven test - **Takes time the first time!**

If everything works as expected you should see some test cases executed successfully!

With Ant
--------
If you are using ant, then the lib folder within the Nadron project contains all the dependent libraries. Just right click and run ant build and it will create the Nadron jar.

*Happy coding!*
