import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class SelectTask {

	
	static int sender;
	static int task_global = 0;
	
	public static void showTasks()
	{
		System.out.println("Tasks are as follows:-");
		System.out.println("1->Broadcast a Text");
		System.out.println("2->Broadcast a File");
		System.out.println("3->Unicast a Text");
		System.out.println("4->Unicast a File");
		System.out.println("5->Blockcast a Text");
		System.out.println("6->Get all online clients");
	}
	
	public static HashMap select_task()
	{
		System.out.println("Input a task you want to perform:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int task = 0;
		try {
			task = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			
		}
		catch(NumberFormatException e)
		{
			System.out.println("I have exited.Bye");
		}
		
		task_global = task;
		
		if(task==1 | task==3 | task==5)
		{
			String to_send_client_name = chooseClientBasedOnTask(task);
			HashMap<Integer,String[]> map = new HashMap<Integer,String[]>();
			map.put(task, new String[]{to_send_client_name,takeMessageInput()});
			return map;
		}
		else if(task==2 | task==4)
		{
			String to_send_client_name = chooseClientBasedOnTask(task);
			HashMap<Integer,HashMap<String,HashMap<String,byte[]>>> map = new HashMap<Integer,HashMap<String,HashMap<String,byte[]>>>();
			HashMap<String,byte[]> path_file = takeFileInput();
			HashMap<String, HashMap<String,byte[]>> temp = new HashMap<String, HashMap<String,byte[]>>();
			temp.put(to_send_client_name, path_file);
			//temp is a hashmap consisting of client name key, associated with Hashmap of 
			//filepath key and byte array consists of the file content
			map.put(task, temp);
			return map;
		}
		else
		{
			HashMap<Integer,String[]> map = new HashMap<Integer,String[]>();
			map.put(task, new String[]{});
			return map;
		}		
	}
	
	
	
	
	public static String takeMessageInput()
	{
		System.out.println("Enter your message input:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg = null;
		try {
			msg = br.readLine();
		} catch (IOException e) {	
			e.printStackTrace();
		}
		return msg;
	}
	
	public static HashMap<String,byte[]> takeFileInput()
	{
		System.out.println("Select the input file:");
		String[] file_path = getFilePath();
		File file = new File(file_path[1]);
		FileInputStream fis = null;
		try {
			byte [] fileBytes=new byte[(int)file.length()];
		    InputStream is = new FileInputStream(file);
		    System.out.println("Total file size to read (in bytes) : "
					+ is.available());
		    int c = 0;
		    while ((c = is.read(fileBytes, 0, fileBytes.length)) > 0) 
	        {}
		    HashMap<String,byte[]> map = new HashMap<String,byte[]>();
		    map.put(file_path[0], fileBytes);
		    return map;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
 
	public static JFileChooser chooser = new JFileChooser();
	public static String[] getFilePath()
	{
		chooser.setVisible(true);
        int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
        }
        return new String[]{chooser.getSelectedFile().getName(),chooser.getSelectedFile().getAbsolutePath()};
	}
	
	public static String chooseClientBasedOnTask(int task)
	{
		String client_name = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(task==1 | task==2)
		{
			client_name = "all";
		}	
		else if(task==3)
		{
			System.out.println("Type the name of the client you want to send a private message:");
			try {
				client_name = br.readLine();
			} catch (IOException e) {	
				e.printStackTrace();
			}
		}
		else if(task==4)
		{
			System.out.println("Type the name of the client you want to send a private file:");
			try {
				client_name = br.readLine();
			} catch (IOException e) {		
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Type the name of the client you don't want to send a message:");
			try {
				client_name = br.readLine();
			} catch (IOException e) {		
				e.printStackTrace();
			}
		}
		return client_name;
	}
}
