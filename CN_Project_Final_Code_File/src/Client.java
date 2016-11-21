import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unchecked")
public class Client {

	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	int username;
	public static int num = 0;
	static String path = "C:\\Users\\Ekam Kalsi\\workspace\\CN Project\\Clients\\";
	
	//Runs the main thread for client//
	void run()
	{
		try
		{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			System.out.println("Connected to localhost in port 8000");
			num++;
			final Thread receive_thread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while(true){
						start_receive_thread(in);
					}
				}
				
			}); 
			receive_thread.start();
			
			final Thread send_thread = new Thread(new Runnable(){

				@Override
				public void run() 
				{
					while(true){
						start_send_thread(out);
					}
				}
			});
			send_thread.start();
			
			
		}
		catch (ConnectException e) {	
			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}

	}
	
	static boolean flag = true;
	//Creates the received file for the client//
	public static void makeFile(HashMap<String, HashMap<String,HashMap<String,byte[]>>> sender_map)
	{
		String sender = "";
		for(String str:sender_map.keySet())
		{
			sender = str;
		}
		
		HashMap<String,HashMap<String,byte[]>> map = sender_map.get(sender);
		
		String client = "";
		for(String str:map.keySet())
		{
			client = str;
		}
		
	    HashMap<String,byte[]> content = map.get(client);
	    String file_name = "";
		for(String str:content.keySet())
		{
			file_name = str;
		}
		
		String[] type_arr = file_name.split("\\.");
		System.out.println(type_arr[0]);
		String type = type_arr[1];
		String[] name_arr = type_arr[0].split("\\\\");
		String name = name_arr[name_arr.length - 1];
		
		byte[] file_content_arr = content.get(file_name);
		
		File file = new File(path + client + "\\" + name + "." + type);
		try {
			OutputStream out = new FileOutputStream(file);
			try {
				out.write(file_content_arr, 0, file_content_arr.length);
				out.close();
				System.out.println("You received the file " + name + "." + type + " from Client " + sender);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	//Manages the receive thread//
	void start_receive_thread(ObjectInputStream in)
	{
		try {
			HashMap msg = (HashMap)in.readObject();
			Set<Integer> set = msg.keySet();
			int task = 0;
			for(int i:set)
			{
				task = i;
			}
			
			//Manages the file receives//
			if(task==2 | task==4)
			{
				HashMap<Integer,HashMap<String, HashMap<String,HashMap<String,byte[]>>>> temp = (HashMap<Integer,HashMap<String, HashMap<String,HashMap<String,byte[]>>>>)msg;
				HashMap<String, HashMap<String,HashMap<String,byte[]>>> map = (HashMap<String, HashMap<String,HashMap<String,byte[]>>>)msg.get(task);
				makeFile(map);
			}
			//Manages the text and online client receive//
			else
			{
				HashMap<Integer,String[]> map = (HashMap<Integer,String[]>)msg;
				if(flag)
				{
				    makeClientFolder(map.get(0)[0]);
					flag = false;
				}
				else
				{
					if(task==1 | task==3 | task==5)
					{
						System.out.println("You received a message from Client " + map.get(task)[2] + ": " + map.get(task)[1]);
					}
					else
					{
						if(map.get(task)[0].equals(""))
						{
							System.out.println("You are the only online client.");
						}
						else
						{
							System.out.println("The online clients are:");
							System.out.println(map.get(task)[0]);
						}
					}
					
				}
			}
			
//			try {
//				System.out.println("Wait a bit!!");
//				TimeUnit.SECONDS.sleep(2);
//			} catch (InterruptedException e) {
//				
//				e.printStackTrace();
//			}
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			System.exit(0);
		}
	}
	
	//Start send thread//
	void start_send_thread(ObjectOutputStream out)
	{
		SelectTask st = new SelectTask();
		st.showTasks();
		Object message = st.select_task();
		//Managing all the file operations//
		if(st.task_global==2 | st.task_global==4)
		{
			HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> info = (HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>)message;
			send_file_to_server(info);
		}
		//Manages all text and getting online client operations//
		else if(st.task_global==1 | st.task_global==3 | st.task_global==5 | st.task_global==6)
		{
			HashMap<Integer,String[]> info = (HashMap<Integer,String[]>)message;
			send_text_to_server(info);
		}
		else
		{
			System.exit(0);
		}
		
	}
	
	//Send any file the server// 
	void send_file_to_server(HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> info)
	{
		try {
			out.writeObject(info);
			out.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	//Send any text to the server//
	void send_text_to_server(HashMap<Integer,String[]> info)
	{
		
		try {
			out.writeObject(info);
			out.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	

	//Create folder for client as soon as it is connected to the server//
	public static void makeClientFolder(String name)
	{
		File file = new File(path + name);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
	}
	
	
	
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}




}