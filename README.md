# InternetChatApplication-CNT5106C

Internet Chat Application for CNT5106C for Fall 2016

This is an Internet Chat Application based on Java Console for the course CNT5106C coursework for fall 2016. We have created a client server model. Here client sends a message or file to different clients via the server.

The Basic Project outline to achieve the following 

(1) Broadcast message: Any client is able to send a text to the server, which will relay it to all other clients for display.

(2) Broadcast file: Any client is able to send a file of any type to the group via the server.

(3) Unicast message: Any client is able to send a private message to a specific other client via the server.

(4) Unicast file : Any client is able to send a private file of any type to a specific other client via the server.

(5) Blockcast message: Any client is able to send a text to all other clients except for one via the sever.

# Menu Driven Environment of the project

Here the client is asked for his preference to send after connecting to the server and he must chose from the following options

1->Broadcast a Text 

2->Broadcast a File

3->Unicast a Text

4->Unicast a File

5->Blockcast a Text

6->Get all online clients

If the client selects option 1 then he/she will be asked for the message that needs to broadcasted. Once the user types the message and clicks on enter, then message will be sent to the server and the server will send the message to the respective clients.

If the client selects option 2 then he/she will be asked for the file that needs to broadcasted. Once the user selects a file and clicks on enter, then file will be sent to the server and the server will send the file to the respective clients.

If the client selects option 3 then he/she will be asked for the message that needs to unicasted and the client to whom that message needs to be unicated to. Once the user enters a text and the client respectively and clicks on enter, then message will be sent to the server and the server will send the message to the unicasted client.

If the client selects option 4 then he/she will be asked for the file that needs to unicasted and the client to whom that file needs to be unicated to. Once the user selects a file and the client respectively and clicks on enter, then file will be sent to the server and the server will send the message to the unicasted client.

If the client selects option 5 then he/she will be asked for the text that needs to blockcasted and the client to whom that message need not sent to.  Once the user selects a text and the client respectively and clicks on enter, then file will be sent to the server and the server will send the message to all the clients except for the mentioned clients.

If the client selects option 6, we will provide all the online clients in the chat group.


# Other Information

Programming language: Java

Operating System: Windows

Programming Tool: Eclipse

# NOTE(Conditions Apply)

1. Currently tested only for localhost environment.

2. It is assumed that the client knows his/her client details.

3. The file path for saving of the broadcasted of files obtained from the serveris hardcoded in the program.

4. The port of the client is always 8000 and cannot be changed dynamically.

5. Here the Client can unicast both file and message to himself.

# Contributors

Shubham Agiwal- File Broadcast, Message Broadcast, Message Unicast

Ekam Kalsy- Message Blockcast,File Unicast, GUI Interface for the file reception for the Client
