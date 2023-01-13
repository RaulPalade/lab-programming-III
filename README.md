# Lab Programming III

The task is to develop a Java application that implements an email service organized with a mail server that manages users' email boxes and the necessary mail clients to allow users to access their email boxes. 
- The mail server should manage a list of email boxes and maintain persistence by using files (txt or binary, your choice, databases cannot be used) to store messages permanently. 
- The mail server should have a graphical interface on which the log of actions taken by the mail clients and events that occur during the interaction between clients and the server is displayed.
  - For example: opening/closing of a connection between mail client and server, sending messages by a client, receiving messages by a client, delivery errors of messages.
  - Note: Do not log local client events such as a user clicking a button or opening a window, as they are not relevant to the server.
- A email account contains:

  - The name of the email account associated with the mailbox (e.g. giorgio@my.mail.com).
  - Potentially an empty list of messages. Email messages are instances of an Email class that specifies the ID, sender, recipient/s, subject, text, and sending date of the message.
 
- The email client, associated with a specific email account, has a graphical interface as follows:

  - The interface allows the user to:
    - create and send a message to one or more recipients (multiple recipients for a single email message)
    - read messages in the mailbox
    - reply to a received message, in Reply (to the sender of the message) and/or Reply-all (to the sender and all recipients of the received message)
    - forward a message to one or more email accounts
    - remove a message from the mailbox.
  - NB: the mail client should not crash if the mail server is shut down - handle connection issues with the mail server by sending appropriate error messages to the user and ensuring that the mail client automatically reconnects to the server when it becomes active again.
    
## Technical Requirements
- For demonstration purposes, assume that there are 3 email users communicating with each other. However, the system should be designed to be scalable to handle many users.
- The application should be developed in Java (JavaFXML) and based on the MVC architecture, with Controller + views and Model, following the principles of the Observer Observable pattern. Note that there should be no direct communication between views and models: all communication between these two levels should be mediated by the controller or supported by the Observer Observable pattern. Do not use deprecated Observer.java and Observable.java classes. Use the JavaFX classes that support the Observer Observable pattern.
- The application should allow the user to correct any input errors (e.g. in case of entering non-existent email addresses, the server should send an error message to the client that sent the message; also, in case of entering syntactically incorrect addresses, the client itself should report the problem without trying to send messages to the server).
- The client and server of the application should parallelize activities that do not require sequential execution and handle any problems of access to resources in mutual exclusion. Note: the mail clients and server must be separate Java applications; the creation/management of messages should take place in parallel with the reception of other messages.
- The application should be distributed (the mail clients and server should all be on separate JVMs) through the use of Java Sockets.

## User Interface Requirements
- The user interface should be:
  - Understandable (transparency). In particular, in case of errors, it should signal the problem to the user.
  - Reasonably efficient to allow the user to perform operations with a minimum number of clicks and data entries.
  - Implemented using JavaFXML and, if necessary, Java Threads. The use of Java Beans, properties, and properties binding is not required but recommended.

