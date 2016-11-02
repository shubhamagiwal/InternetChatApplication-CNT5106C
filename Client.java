import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	String serverName="localhost";
	int portNumber=8000;
	String filePath="/Users/shubhamagiwal/Desktop/CNT5106C/CNT5106C_localFiles/CNT5106C_Fall2016/src/";
	int clientNum;
	String clientName="";
	int broadCastNumberOfParameters=3;
	int blockCastNumberOfParameters=4;
	int unicastCastNumberOfParameters=4;

	
	public Client() {}
	
	boolean commandVerification (List command){
		boolean flag=false;
		switch ((String)command.get(0)){
		case "broadcast":
			if(command.size()==broadCastNumberOfParameters){
				if(command.get(1).equals("message") || command.get(1).equals("file")){
					flag=true;
				}
				else{
					System.err.println("The given command "+ command.get(0)+" with parameter"+command.get(1) +" is not supported");

				}
			}else{
				System.err.println("The "+command.get(0)+" command has less numbers of parameters" );
			}
			break;
		case "blockcast":
			if(command.size()==blockCastNumberOfParameters){
				if(command.get(1).equals("message") || command.get(1).equals("file")){
					flag=true;
				}
				else{
					System.err.println("The given command "+ command.get(0)+" with parameter"+command.get(1) +" is not supported");
				}
			}else{
				System.err.println("The "+command.get(0)+" command has less numbers of parameters" );
			}
		case "unicast":
			if(command.size()==unicastCastNumberOfParameters){
				if(command.get(1).equals("message") || command.get(1).equals("file")){
					flag=true;
				}
				else{
					System.err.println("The given command "+ command.get(0)+" with parameter"+command.get(1) +" is not supported");
				}
			}else{
				System.err.println("The "+command.get(0)+" command has less numbers of parameters" );
			}
		case "getClients": flag=true; 
		                    break;
		case "getId": flag=true;
					  break;
		
		default:
			System.err.println("The given command "+ command.get(0)+" is not supported");
		}
		
		return flag;
	}

	void run()
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket(serverName, portNumber);
			commandList();
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
								String type=(String)in.readObject();
								MESSAGE = (String)in.readObject();
								String fileName="";
								int fileLength=0;
								//String clientName="";
								byte b[]=null;									
								 if(type.equals("FILE")){
									fileName=(String)in.readObject();
									fileLength=in.readInt();									
									clientName=(String)in.readObject();
									b=new byte[fileLength];
									in.readFully(b, 0, fileLength);
									if(Files.isDirectory(Paths.get(filePath+clientName))){
										FileOutputStream fos = new FileOutputStream(filePath+clientName+"/"+fileName);
										fos.write(b);
										fos.close();
									}else{
										new File(filePath+clientName).mkdir();
										FileOutputStream fos = new FileOutputStream(filePath+clientName+"/"+fileName);
										fos.write(b);
										fos.close();
									};
									System.out.println(MESSAGE);
								}else if(type.equals("MESSAGE")){
									System.out.println(MESSAGE);
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}catch (EOFException e){
								System.out.println("The Server has been ShutDown");
								System.exit(0);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}; 
			
			receiveThread.start();
			
			final Thread inputThread = new Thread() {
				@Override
				public void run() {
					Scanner in = null;
					try {
						in = new Scanner(System.in);
						while (true) {
							String line = in.nextLine();
							List<String> list = new ArrayList<String>();
							Matcher match = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
							while (match.find()){
							    list.add(match.group(1).replace("\"", ""));
							}
							
							if(commandVerification(list)){
								out.writeObject(line);
								if(list.size()>1 && list.get(1).equals("file")){
									File f2=new File(list.get(2));
									out.writeObject("FILE");
									out.writeObject(f2.getName());
									out.writeObject((int)f2.length());
									byte [] fileBytes=new byte[(int)f2.length()];
									InputStream is = new FileInputStream(f2);
									 int c = 0;
								        while ((c = is.read(fileBytes, 0, fileBytes.length)) > 0) {
								            out.write(fileBytes, 0, c);
								        }        
								}else if(list.size()>1 && list.get(1).equals("message")){
									out.writeObject("MESSAGE");
								}else if(list.size()==1 && list.get(0).equals("getClients")){
									out.writeObject("GETCLIENTS");
								}else if(list.size()==1 && list.get(0).equals("getId")){
									out.writeObject("GETID");
								}
								out.flush();
							}else{
								commandList();
							}
						}
					} catch (Exception e) {
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
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
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
	
	void commandList(){
		System.out.println("The Commands available for this Application are as follows:");
		System.out.println("Broadcasting a message: broadcast message <\"message In quotes\">");
		System.out.println("Unicasting a message : unicast message <\"message In quotes\"> <clientnum[starts from 0 for the first client]> ");
		System.out.println("Blockcasting a message : blockcast message <\"message In quotes\"> <clientnum[starts from 0 for the first client] which you don't want to sent to.> ");
		System.out.println("Checking For Available clients: getClients");
		System.out.println("Broadcasting a File : broadcast file <\"filePath In quotes\"> ");
		System.out.println("Unicasting a File : unicast file <\"filePath In quotes\"> <clientnum[starts from 0 for the first client]> ");
		System.out.println("Blockcasting a file : blockcast file <\"filePath In quotes\"> <clientnum[starts from 0 for the first client] which you don't want to send it too>");
		System.out.println("Get Your Client Id: getId");
	}
	//main method
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}

}
