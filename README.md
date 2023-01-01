# Server

This is a simple chat server I built to study basic networking, socket programming, concurrency, cryptography(AES, RSA and Hashing) and connecting with database (MySQL and FireStore)

### How it works

- Ask for user input for the port number through the console using **Scanner**
- Start a server socket and wait for the connection
- When a client connects, create a new thread using a connection object
  - The connection class is for communication between the server and the client using a socket.
  - When a connection is made, it listens for the packet object sent from the client
  - Packet class models data transferred between client and server using **socket** and **ObjectInputStream and ObjectOutputStream**
- When a connection receives a packet, use the RSA encryption object to decrypt packet keys. These keys are for AES encryption. Use AES to decrypt messages in packets.
  - Use **Java crypto and security** to implement the AES and RSA algorithm for encryption and decryption
- If the packet’s header is login or signup, use the database class to handle inserting and querying data from the database
  - Use **Java sql** to connect Java and MySQL
- If the packet’s header is message, insert the message, sender, and recipient to Firestore using **google.cloud, google.firebase, google.api andgoogle.auth**