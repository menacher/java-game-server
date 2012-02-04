This is a java nio based server specifically designed for mutliplayer games. It supports UDP and TCP transports. It uses [JBoss Netty](http://netty.io/) for high speed network transmission and [Jetlang](http://code.google.com/p/jetlang/ "jetlang") for extremely fast in-vm message passing between player sessions and game rooms. The project also uses spring for its dependency injection. This way, it is highly configurable and you can swap out any part of the server with your own implementations.

Installation
============
**Pre-requisites**: Please have maven 3+ and [Spring source tool suite](http://www.springsource.com/developer/sts "STS") or eclipse installed. If you are using plain vanilla eclipse, then M2Eclipse and EGit plugins need to be installed. If you are using another IDE then the maven-eclipse plugin part in the pom.xml needs to be modified. This project has some AspectJ aspects, but it is not necessary for clients to use them.

Steps
-----
1.  git clone git@github.com:menacher/java-game-server.git
2.  cd java-game-server
3.  cd jetserver
4.  mvn eclipse:eclipse - **Takes time, the first time!** If you want to reduce this time, then comment out include sources/jars option from the maven pom.xml eclipse plugin part.
5.  eclipse -> file -> import -> git -> select repository and import jetserver project.
6.  jetserver project in eclipse -> right click on pom.xml -> run as -> maven test - **Takes time the first time!**

If everything works as expected you should see some test cases executed successfully!

*Happy coding!*