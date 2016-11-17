# InternetChatApplication-CNT5106C
Internet Chat Application for CNT5106C for Fall 2016

This is an Internet Chat Application based on Java Console for the course CNT5106C coursework for fall 2016.

We have created a client server model. Here client sends a message or file to different clients via the server.

The Basic Project outline to achieve the following 

1. Broadcast of the raw message to all the clients.

2. Unicast of the raw message to a particular client.

3. Blockcast of the raw message to all the clients expect for the specified client.

4. Broadcast of a file to all the clients.

5. Unicast of a file to a particular client.

Here the client is asked for his preference to send after connecting to the server and he must chose from the following options

1->Broadcast a Text 

2->Broadcast a File

3->Unicast a Text

4->Unicast a File

5->Blockcast a Text

6->Get all online clients

Based on the above options from 1 to 6 approriate inputs are asked from the client to be transfered to other client/clients. 

NOTE(Conditions Apply)

1. Currently tested for localhost environment.

2. It is assumed that the client knows his/her client details.

3. The file path where the directories for client to receive files is hardcoded in the programs.

4. The port of the client is always 8000 and cannot be changed dynamically.

5. Here the Client can unicast both file and message to himself.
