# Nadron Client

This is a Java client library for the [Nadron](https://github.com/menacher/java-game-server/tree/netty4/nadron) game server.

## Requirements

- **Java 17 or later**
- **Maven 3.8+** (for building)

## Maven Dependency

```xml
<dependency>
    <groupId>com.github.menacher</groupId>
    <artifactId>nadclient</artifactId>
    <version>0.8-SNAPSHOT</version>
</dependency>
```

## Technology Stack

- **Java 17 LTS**
- **Netty 4.1.115** - High-performance async network framework
- **JUnit 5.11.3** - Modern testing framework

## Usage as Game Client

The general usage steps:

1. Add `nadclient-0.8-SNAPSHOT.jar` and its dependencies to your project classpath (or use Maven)
2. Create `LoginBuilder`, session and `SessionEventHandler` as shown in test classes
3. The `example-games` project has a `ZombieNadClient` which demonstrates a complete example
4. Use the `SessionEventHandler` to accept events from the remote server and to send events back
5. To send to the remote Nadron server, create a TCP/UDP network event:
   ```java
   Event event = Events.networkEvent(messageBuffer);
   // or for UDP
   Events.networkEvent(messageBuffer, DeliveryGuaranty.Fast);
   ```
6. Call `session.onEvent(event);` to transmit the event to the remote Nadron server

## Building from Source

```bash
git clone git@github.com:menacher/java-game-server.git
cd java-game-server/nadclient
mvn clean install
```

## Running Tests

```bash
mvn test
```

All 7 tests pass successfully on Java 17 with Netty 4.1.         