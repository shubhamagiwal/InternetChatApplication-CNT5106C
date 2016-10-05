import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.Scanner;

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	String serverName="localhost";
	int portNumber=8000;

	public void Client() {}

	void run()
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket(serverName, portNumber);
			System.out.println(requestSocket);
			System.out.println("Connected to"+serverName+"on port"+portNumber);
			//initialize inputStream and outputStream
			
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
			
			// Starting a Thread to receive data from Server to the client
			final Thread receiveThread = new Thread(){
				public void run() {
					try {
						out = new ObjectOutputStream(requestSocket.getOutputStream());
						out.flush();
						in = new ObjectInputStream(requestSocket.getInputStream());
						while(true){
							try {
								MESSAGE = (String)in.readObject();
								System.out.println("Receive message: " + MESSAGE);	
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}; 
			
			receiveThread.start();
			
			final Thread inputThread = new Thread() {
				@Override
				public void run() {
					// Use a Scanner to read from the remote server

					Scanner in = null;
					try {
						in = new Scanner(System.in);
						while (true) {
							System.out.println("Enter your text");
							String line = in.nextLine();
							out.writeObject(line);
							out.flush();
						}
					} catch (Exception e) {
//						e.printStackTrace();
					} finally {
						if (in != null) {
							in.close();
						}
					}
				}
			};
			
			inputThread.start();
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
//		catch ( ClassNotFoundException e ) {
//            		System.err.println("Class not found");
//        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
//		finally{
//			//Close connections
//			try{
//				in.close();
//				//out.close();
//				requestSocket.close();
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//main method
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}

}
