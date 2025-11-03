Nadron is a Java NIO based server specifically designed for multiplayer games. It supports UDP and TCP transports. It uses [Netty 4.1](http://netty.io/) for high speed network transmission and [Jetlang](http://code.google.com/p/jetlang/ "jetlang") for extremely fast in-VM message passing between player sessions and game rooms. The project uses Spring Framework 6 for dependency injection, making it highly configurable so you can swap out any part of the server with your own implementations.

## Technology Stack

- **Java 17 LTS** - Modern, long-term support release
- **Netty 4.1.115** - High-performance async network framework
- **Spring Framework 6.2.0** - Dependency injection and application context
- **Jackson 2.18.1** - JSON serialization
- **JUnit 5.11.3** - Modern testing framework
- **Log4j 2.24.2** - Secure, modern logging
- **SLF4J 2.0.16** - Logging abstraction

Wiki
====
The [wiki](https://github.com/menacher/java-game-server/wiki) provides implementation level details and answers to general questions that a developer starting to use Nadron server might have about it. This [blog post](http://nerdronix.blogspot.com/2013/06/creating-multiplayer-game-using-html-5.html) contains a decent tutorial on creating a multiplayer game using this server. 
Support Group
=============
For general discussion please use the [Nadron server google group](https://groups.google.com/forum/#!forum/jetserver). Issues and bugs can be raised directly in github.
## Requirements

- **Java 17 or later** - The project uses Java 17 LTS features
- **Maven 3.8+** - For building the project

## Installation

### Maven
```xml
<dependency>
    <groupId>com.github.menacher</groupId>
    <artifactId>nadron</artifactId>
    <version>0.8-SNAPSHOT</version>
</dependency>
```

### Building from Source

**Prerequisites:**
- Java 17 JDK or later
- Maven 3.8 or later
- Git

**Steps:**

1. Clone the repository:
   ```bash
   git clone git@github.com:menacher/java-game-server.git
   cd java-game-server/nadron
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run tests:
   ```bash
   # Run regular tests (excludes performance tests)
   mvn test

   # Run performance tests
   mvn test -Pperformance
   ```

4. The built JAR will be in `target/nadron-0.8-SNAPSHOT.jar`

### IDE Setup

**IntelliJ IDEA / Eclipse / VS Code:**
- Simply import as a Maven project
- The IDE will automatically download dependencies
- Make sure your project SDK is set to Java 17+

**For Eclipse users:**
```bash
mvn eclipse:eclipse
```
Then import the project via File → Import → Existing Projects into Workspace

## Performance

The modernized Nadron server delivers excellent performance on Java 17:

- **Message passing rate**: Up to 40+ million messages/sec between sessions
- **Event dispatching**: 5 million events in ~0.6 seconds
- **Session communication**: 10-40 million messages/sec depending on configuration

## Testing

The project includes comprehensive unit tests using JUnit 5:

- **13 regular tests** - Fast unit tests that run with every build
- **4 performance tests** - Tagged separately for performance benchmarking


*Happy coding!*
