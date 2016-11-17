import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number
	private static HashMap<Integer,String> client_map = new HashMap<Integer,String>();
	static int clientNum = 0;
	private static ObjectInputStream in;	//stream read from the socket
	private static ArrayList<ObjectOutputStream> out_list = new ArrayList<ObjectOutputStream>();    //stream write to the socket
	
	private static class Handler extends Thread 
	{
    	private Socket connection;
    	private int no;		//The index number of the client
    	
    	public Handler(Socket connection, int no)
    	{
        	this.connection = connection;
        	System.out.println(no);	
    		this.no = no;
    	}

        @SuppressWarnings("unchecked")
		public void run() 
        {
        	try
        	{
        		ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            	out.flush();
            	out_list.add(out);
    			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
    			HashMap<Integer,String[]> temp = new HashMap<Integer,String[]>();
    			temp.put(0, new String[]{clientNum + ""});
    			out.writeObject(temp);
    			clientNum++;
    			while(true)
    			{
    				try {
						//HashMap<Integer,String[]> info = (HashMap<Integer, String[]>) in.readObject();
						HashMap info = (HashMap)in.readObject();
						int task = 0;
						for(int i:(Set<Integer>)info.keySet())
						{
							task = i;
						}
						if(task==1 |task==3 |task==5 | task==6)
						{
							HashMap<Integer,String[]> map = (HashMap<Integer,String[]>)info;
							decodeAndSendMessage(task,map.get(task));
						}
						else
						{
							HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> map = (HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>)info;
							decodeAndSendFile(task,map.get(task));
						}
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
    			}

    			
    			
        	}catch(IOException ioException){
        		out_list.remove(this.no);
			}
        	
        	
//	 		try
//	 		{
//	 			//Initialize a single output and input stream
////	 			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
////				out.flush();
////	 			in = new ObjectInputStream(connection.getInputStream());
//	 			//Add to the list of other output and input streams, hence creating multiple server client connections//
//	 			//out_list.add(out);
//	 			try
//	 			{
//					while(true)
//					{
//						System.out.println("Client" + this.no);
//						//decodeAndSendMessage(task,message_by_client);
//					}
//				}
//				catch(ClassNotFoundException classnot){
//						System.err.println("Data received in unknown format");
//					}
//			}
//			catch(IOException ioException){
//				System.out.println("Disconnect with Client " + no);
//			}
//			finally{
//				//Close connections
//				try
//				{
////					in.close();
////					out_list.get(clientNum).flush();
////					connection.close();
//				}
//				catch(IOException ioException){
//					System.out.println("Disconnect with Client " + no);
//				}
//			}
	}

//		public void sendMessage(String msg)
//		{
//			try
//			{
//				out.writeObject(msg);
//				out.flush();
//				System.out.println("Send message: " + msg + " to Client " + no);
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}

        
        public void decodeAndSendFile(int task,HashMap<String,HashMap<String,byte[]>> map)
        {
        	if(task==2)
        	{
        		for(int i=0;i<out_list.size();i++)
				{
					sendFile(out_list.get(i),map,task,i);
				}
        	}
        	else
        	{
        		String client = "";
        		for(String str:map.keySet())
        		{
        			client = client + str;
        		}
        		
        		for(int i=0;i<out_list.size();i++)
        		{
        			if(i==Integer.parseInt(client))
        			{
        				sendFile(out_list.get(i),map,task,i);
        			}
        		} 
        	}
        }
        
        

		public void decodeAndSendMessage(int task,String[] arr)
		{
			System.out.println("I am here");
			HashMap<Integer,String[]> map = new HashMap<Integer,String[]>();
			map.put(task, arr);
			//broadcast message or file//
			if(task==1 | task==2)
			{
				for(int i=0;i<out_list.size();i++)
				{
					if(task==1)
					{
						sendMessage(out_list.get(i),map);
					}
					
				}
			}
			
			
			//Unicast message or file
			else if(task==3 | task==4)
			{
				String client = arr[0];
				System.out.println("The client is " + client);
				for(int i=0;i<out_list.size();i++)
				{
					if(i==Integer.parseInt(client))
					{
						if(task==3)
						{
							System.out.println("hey i am here");
							sendMessage(out_list.get(i),map);
							return;
						}
						
					}
				}
			}
			
			//Blockcast message//
			else if(task==5)
			{
				for(int i=0;i<out_list.size();i++)
				{
					String client = arr[0];
					if(i!=Integer.parseInt(client))
					{
						sendMessage(out_list.get(i),map);
					}
				}
			}
			
			else
			{
				System.out.println(this.no);
				String msg = "";
				for(int i=0;i<out_list.size();i++)
				{
					if(i!=this.no)
					{
						msg = msg + "Client" + i + "\n"; 
					}
				}
				HashMap<Integer,String[]> send_map = new HashMap<Integer,String[]>();
				send_map.put(task, new String[]{msg});
				sendMessage(out_list.get(this.no),send_map);
			}
			
		}
		
		
		
		public void sendMessage(ObjectOutputStream out,HashMap<Integer,String[]> info)
		{
			try
			{
				System.out.println("I am here");
				out.writeObject(info);
				out.flush();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		public void sendFile(ObjectOutputStream out,HashMap<String,HashMap<String,byte[]>> msg,int task,int receiver_client)
		{
			try
			{
				String temp = "";
				for(String str:msg.keySet())
				{
					temp = str;
				} 
				
				HashMap<String,byte[]> file_content = msg.get(temp);
				
				HashMap<String,HashMap<String,byte[]>> map = new HashMap<String,HashMap<String,byte[]>>();
				map.put(receiver_client+"", file_content);
				
				HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> return_map = new HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>();
				return_map.put(task, map);
				
				out.writeObject(return_map);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		
		
		
		
	
	}
	
	
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("The server is running."); 
        ServerSocket listener = new ServerSocket(sPort);
		try 
    	{
    		while(true) 
    		{
        		new Handler(listener.accept(),clientNum).start();
        		System.out.println("Client "  + clientNum + " is connected!");
        	}
    	} 
    	finally 
    	{
        		listener.close();
    	} 
 
    }

	


}