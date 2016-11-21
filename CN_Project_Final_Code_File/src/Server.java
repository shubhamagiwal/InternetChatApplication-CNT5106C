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
        	this.no = no;
    	}

    	//Runs the main thread for Server//
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
						e.printStackTrace();						
					}
    			}
        	}catch(IOException ioException){
        		System.out.println("Client " + this.no + "is disconnected");
        		out_list.set(this.no, null);
			}
	}


        //Manages the task of decoding and sending the file to various clients//
        public void decodeAndSendFile(int task,HashMap<String,HashMap<String,byte[]>> map)
        {
        	if(task==2)
        	{
        		for(int i=0;i<out_list.size();i++)
				{
					if(i!=this.no & out_list.get(i)!=null)
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
        			if(i==Integer.parseInt(client) & out_list.get(i)!=null)
        			{
        				sendFile(out_list.get(i),map,task,i);
        			}
        		} 
        	}
        }
        
        
        //Decode and send message, also send online client information//
		public void decodeAndSendMessage(int task,String[] arr)
		{
			HashMap<Integer,String[]> map = new HashMap<Integer,String[]>();
			if(task!=6)
			{
				String[] temp_arr = new String[arr.length + 1];
				temp_arr[0] = arr[0];
				temp_arr[1] = arr[1];
				temp_arr[2] = this.no + "";
				map.put(task, temp_arr);
			}
			
			//broadcast message or file//
			if(task==1 | task==2)
			{
				for(int i=0;i<out_list.size();i++)
				{
					if(task==1)
					{
						if(i!=this.no & out_list.get(i)!=null)
							sendMessage(out_list.get(i),map);
					}
					
				}
			}
			
			
			//Unicast message or file
			else if(task==3 | task==4)
			{
				String client = arr[0];
				for(int i=0;i<out_list.size();i++)
				{
					if(i==Integer.parseInt(client))
					{
						if(task==3 & out_list.get(i)!=null)
						{
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
					if(i!=Integer.parseInt(client) & out_list.get(i)!=null)
					{
						sendMessage(out_list.get(i),map);
					}
				}
			}
			
			else
			{
				String msg = "";
				for(int i=0;i<out_list.size();i++)
				{
					if(i!=this.no & out_list.get(i)!=null)
					{
						msg = msg + "Client" + i + "\n"; 
					}
				}
				HashMap<Integer,String[]> send_map = new HashMap<Integer,String[]>();
				send_map.put(task, new String[]{msg});
				sendMessage(out_list.get(this.no),send_map);
			}
			
		}
		
		
		//send message to client, task->message//
		public void sendMessage(ObjectOutputStream out,HashMap<Integer,String[]> info)
		{
			try
			{
				out.writeObject(info);
				out.flush();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		
		//Send file to client(s) using the formt, task->receiver_client->file_content(path,data)//
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
				
				HashMap<String, HashMap<String,HashMap<String,byte[]>>> sender_map = new HashMap<String, HashMap<String,HashMap<String,byte[]>>>();
				sender_map.put(this.no + "", map);
				
				HashMap<Integer,HashMap<String, HashMap<String,HashMap<String,byte[]>>>> return_map = new HashMap<Integer,HashMap<String, HashMap<String,HashMap<String,byte[]>>>>();
				return_map.put(task, sender_map);
				
				out.writeObject(return_map);
				out.flush();
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