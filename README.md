# chatDemo

## NOTES:
1. Based on wireshark logs, it would appear that the `message-tools.jar` generator attempts to create a new
chat message (POST /chats/{id}/messages) *BEFORE* it creates the chat (POST /chats). This doesn't seem to happen
consistently, so I don't know whether the generator is purposely doing this or there's a bug. I've committed a
wireshark session: `wireshark_logs.pcapng`
1. I implemented most of the unit tests, but didn't get around to doing end-to-end endpoint validation tests. 
I would have wanted to use some common frameworks to validate good and bad endpoint requests.
1. This implementation makes use of in-memory store for the chats, messages, and users. Obviously, that doesn't
scale, so a future consideration would be a robust persistent storage outside of the app.
1. I purposely omitted error messages for POST requests in which the error checking detected incompatible
users (not in each other's contacts), etc. The idea is to *not* give any hints as to why a 403 was returned.
1. I would have preferred to use a REST framework such as DropWizard or Spring, but based on the first requirements
listed in the SERVER section of the instuctions I chose to implementation my own simple framework around
JDK 11's HttpServer class. An opensource framework also would have allowed me to have better built-in
POST body validation and reporting, rather than handcrafting it myself.

## TODOs:
1. Add endpoint for managing create/update/delete users, including their contacts.
1. Investigate better ways of arbitrating message timestamp collision in a given Chat
1. Add swagger definition and swaggerUI support. 
1. Design scalable external datastore for users, chats, messages (instead of in-memory). 
Consider sharding of data based on messageIds ?
1. Use log4j or other logging framework
1. Add end-to-end endpoint validation, with mocked data
1. Refactor RestServerImpl, et al, to use modern WS framework, eg., SpringBoot or Dropwizard, etc., to
provide more robust routing based on paths, verbs and Accept header.

## Description
Demo chat server. This server demonstrates a RESTful chat server that supports the following endpoints:

* POST: /chats
  * Creates a chat between two users.

* POST: /chats/{chatId}/messages
  * Adds a message to a chat.

* GET: /chats?userId={userId}
  * Lists a user’s current chats.

* GET: /chats/{chatId}/messages
  * Lists a chat’s messages.
  
The server listens on port 8080 on localhost.

## Development

This application uses Java SDK 11. Before building or running this application, ensure you have downloaded and
installed [JDK 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html).
You should also declare JAVA_HOME to point to the JDK 11 distribution, eg. 

### MacOS

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.3.jdk/Contents/Home
```

or

```bash
export JAVA_HOME=`/usr/libexec/java_home -v 11`
```

### Linux

***TODO: verify this***
```bash
export JAVA_HOME=/usr/java/jdk11.0.3/bin/java
```

This application uses gradle as the build tool:

* Build: `./gradlew build`
* Test: `./gradlew test`
* Run: `./gradlew run`

This demo application has a predefined set of userIds and their respective contacts. This file is defined in JSON
and exists in `src/main/resources/contacts.json`. The contacts file is read once at application startup.

## Data and Operational Requirements
* Chat messages must remain consistent.
* Chat messages must be sorted chronologically.
* Chat messages may arrive out of order.
* A chat can only be created between users that are in each other’s contact lists.
* A chat object has the following fields:
  * id | long - the chat’s id
  * participantIds | array[long] - the ids of the participants
* A message object has the following fields:
  * sourceUserId | long - the sender’s user id
  * destinationUserId | long - the recipient's user id
  *  timestamp | long - the epoch millis that correspond with when the message was sent
  * message | string - the body of the message
  
## Data Schemas
### Chat
```$xslt
   {
     "type": "object",
     "required": [
       "id",
       "participantIds"
     ],
     "properties": {
       "id": {
         "type": "integer",
         "examples": [
           23423
         ]
       },
       "participantIds": {
         "type": "array",
         "maxItems": 2,
         "uniqueItems": true,
         "items": {
           "type": "integer",
           "examples": [
             35682,
             35682
           ]
         }
       }
     }
   }
```

### Message
```$xslt
{
   "type": "object",
   "required": [
     "id",
     "timestamp",
     "message",
     "sourceUserId",
     "destinationUserId"
   ],
   "properties": {
     "id": {
       "type": "string",
       "examples": [
         "8911889c-8b93-4786-bbf3-50d56868b309"
       ]
     },
     "timestamp": {
       "type": "integer",
       "examples": [
         1549670552842
       ]
     },
     "message": {
       "type": "string",
       "examples": [
         "Mesh wire keeps chicks inside."
       ]
     },
     "sourceUserId": {
       "type": "integer",
       "examples": [
         26946
       ]
     },
     "destinationUserId": {
       "type": "integer",
       "examples": [
         35682
       ]
     }
   }
 }

```