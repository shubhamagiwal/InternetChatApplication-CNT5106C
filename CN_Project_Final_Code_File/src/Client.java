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
	static String path = "C:\\Users\\Ekam Kalsi\\workspace\\CN Project\\Clients\\"; // Important- File Path that needs to be changed for File transfer to the Client based on different systems. 
	
	public Client() 
	{
		
	}
	
	
	

	
	
	
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
			System.out.println(num);
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
			
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
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
//				System.out.println("here!!");
//				in.close();
//				out.close();
//				requestSocket.close();
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}
	}
	
	static boolean flag = true;
	
	public static void makeFile(HashMap<String,HashMap<String,byte[]>> map)
	{
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
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
			
			
			if(task==2 | task==4)
			{
				HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> temp = (HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>)msg;
				HashMap<String,HashMap<String,byte[]>> map = (HashMap<String,HashMap<String,byte[]>>)msg.get(task);
				makeFile(map);
			}
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
						System.out.println("You received a message: " + map.get(task)[1]);
					}
					else
					{
						System.out.println("You received a message: " + map.get(task)[0]);
					}
				}
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void start_send_thread(ObjectOutputStream out)
	{
		SelectTask st = new SelectTask();
		st.showTasks();
		Object message = st.select_task();
		if(st.task_global==2 | st.task_global==4)
		{
			HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> info = (HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>)message;
			send_file_to_server(info);
		}
		else
		{
			HashMap<Integer,String[]> info = (HashMap<Integer,String[]>)message;
			send_text_to_server(info);
		}
		
	}
	
	void send_file_to_server(HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> info)
	{
		try {
			out.writeObject(info);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	void send_text_to_server(HashMap<Integer,String[]> info)
	{
		
		try {
			out.writeObject(info);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
	public static void makeClientFolder(String name)
	{
		File file = new File("C:\\Users\\Ekam Kalsi\\workspace\\CN Project\\Clients\\" + name);
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
//		HashMap<String,Client> client_map = new HashMap<String,Client>();
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("Enter a username:");
//		String name = "";
//		try {
//			name = name + br.readLine();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Client client = new Client();
		client.run();
			
			
			
		
				
	}




}