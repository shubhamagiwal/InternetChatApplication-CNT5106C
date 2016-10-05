import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number
	static Map<Integer,Socket> clients = new HashMap<Integer,Socket>();
	static Map<Integer,ObjectOutputStream> clientOutputStreams = new HashMap<Integer,ObjectOutputStream>();

	
	public static void main(String[] args) throws Exception {
		System.out.println("The server is running."); 
        ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 0;
        	try {
            		while(true) {
            				new Handler(listener.accept(),clientNum).start();
            				BroadCast broadCast=new BroadCast(clients.get(clientNum),"Client"+clientNum+" is Connected");
            				broadCast.broadCastToAll(clientNum);
            				clientNum++;
            			}
        	} finally {
            		listener.close();
        	} 
 
    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
        private String message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
		private int no;		//The index number of the client

        	public Handler(Socket connection, int no) {
            	this.connection = connection;
	    		this.no = no;
	    		clients.put(this.no, this.connection);
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			clientOutputStreams.put(no, new ObjectOutputStream(connection.getOutputStream()));
			in = new ObjectInputStream(connection.getInputStream());
			while (true){
				try {
					String m=(String)in.readObject();
					System.out.println(m.equals("broadcast"));
					if(m.equals("broadcast")){
						BroadCast broadCast1=new BroadCast(clients.get(this.no),m);
          				broadCast1.broadCastToAll(this.no);
          				m="";
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Client " + no);
		}
	}

	//send a message to the output stream
	public void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
		   }
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
    }
    	
    public static class BroadCast{
    	private Socket connection;
        private ObjectOutputStream out;    //stream write to the socket
        private String broadCastMessage;
        
        BroadCast(Socket newClientConnected){
        	this.connection=newClientConnected;
        }
        
        BroadCast(String message){
        	this.broadCastMessage=message;
        }
        
        BroadCast(Socket newClientConnected,String message){
        	this.broadCastMessage=message;
        	this.connection=newClientConnected;
        }
        
        public void broadCastToAll(int clientNum){
        	int i=0;
        	for(i=0;i<clientOutputStreams.size();i++){
        		if(clients.containsKey(i) && clients.get(i) !=clients.get(clientNum)){
        			try {
        	        	out=clientOutputStreams.get(i);
						out.writeObject(this.broadCastMessage);
						out.flush();
						//out.reset();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        	}
        }
        
    }
}