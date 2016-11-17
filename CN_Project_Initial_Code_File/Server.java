import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number
	static Map<Integer,Socket> clients = new HashMap<Integer,Socket>();
	static Map<Integer,ObjectOutputStream> clientOutputStreams = new HashMap<Integer,ObjectOutputStream>();
	static Set<Integer> clientSet=new HashSet<Integer>();

	
	public static void main(String[] args) throws Exception {
		System.out.println("The server is running."); 
        ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 0;
        	try {
            		while(true) {
            				new Handler(listener.accept(),clientNum).start();
            				System.out.println("Client"+clientNum+" is Connected");
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
	    		clientSet.add(this.no);
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			clientOutputStreams.put(no, new ObjectOutputStream(connection.getOutputStream()));
			in = new ObjectInputStream(connection.getInputStream());
			while (true){
				try {
					String n="";
					int length=0;
					byte[]b=null;
					String m=(String)in.readObject();
					String type=(String)in.readObject();
					//System.out.println(m+" "+type);
					if(type.equals("FILE")){
						n=(String)in.readObject();
						length=(int)in.readObject();
						b=new byte[length];
					    in.readFully(b, 0, length);
						//System.out.println(new String(b));
						//System.out.println(n+" "+length);	
					}
						
					List<String> list = new ArrayList<String>();
					Matcher match = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(m);
					while (match.find()){
					    list.add(match.group(1).replace("\"", ""));
					}
					//System.out.println(m+" "+list);
					if(list.get(0).equals("broadcast") && list.get(1).equals("message")){
						BroadCast broadCast1=new BroadCast(clients.get(this.no),list.get(2));
          				broadCast1.broadCastToAll(this.no);
          				m="";
					}else if(list.get(0).equals("unicast") && list.get(1).equals("message")){
						BroadCast broadCast2=new BroadCast(clients.get(this.no),list.get(2));
          				broadCast2.broadCastToOne(this.no,Integer.parseInt(list.get(3)));
          				m="";
					}else if(list.get(0).equals("blockcast") && list.get(1).equals("message")){
						BroadCast broadCast2=new BroadCast(clients.get(this.no),list.get(2));
          				broadCast2.broadCastMessageToAllExceptOne(this.no,Integer.parseInt(list.get(3)));
          				m="";
					}else if(list.get(0).equals("getClients")){
						BroadCast broadCast3=new BroadCast(clients.get(this.no));
						broadCast3.getAllClientDetails(this.no);
						m="";
					}else if(list.get(0).equals("broadcast") && list.get(1).equals("file")){
						BroadCast broadCast4=new BroadCast(clients.get(this.no));
						broadCast4.broadCastFileToAll(this.no,n,b,length);
					}else if(list.get(0).equals("unicast") && list.get(1).equals("file")){
						BroadCast broadCast4=new BroadCast(clients.get(this.no));
						broadCast4.broadCastFileToOne(this.no,Integer.parseInt(list.get(3)),n,b,length);
					}else if(list.get(0).equals("blockcast") && list.get(1).equals("file")){
						BroadCast broadCast4=new BroadCast(clients.get(this.no));
						broadCast4.broadCastFileToAllExceptOne(this.no,Integer.parseInt(list.get(3)),n,b,length);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		catch(IOException ioException){
			//ioException.printStackTrace();
			System.out.println("Disconnect with Client " + this.no);
			clients.remove(this.no);
			clientOutputStreams.remove(this.no);
			clientSet.remove(this.no);

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
        
        public void broadCastToOne(int sender,int receiver ){
        	Set set= clientOutputStreams.entrySet();
        	Iterator it=set.iterator();
        	String message;
        	while(it.hasNext()){
        		Map.Entry pair=(Map.Entry)it.next();
	        	message="@"+"client"+sender+":"+this.broadCastMessage;
                if((int)pair.getKey()!=sender && (int)pair.getKey()==receiver){
                	try{
                		out=(ObjectOutputStream)pair.getValue();
        	        	out.writeObject("MESSAGE");
                		out.writeObject(message);
 	    	        	out.flush();
                	} catch (IOException e) {
    					e.printStackTrace();
    				}
                }
        	}
        }
        
        public void broadCastToAll(int clientNum){
        	 String message;
        	 Set set = clientOutputStreams.entrySet();
        	 Iterator it = set.iterator();
        	 while(it.hasNext()){
        		 Map.Entry pair = (Map.Entry)it.next();
 	        	 message="@"+"client"+clientNum+":"+this.broadCastMessage;
 	        	 if((int)pair.getKey()!=clientNum){
 	        		 try{
 	    	        	 out=(ObjectOutputStream)pair.getValue();
 	    	        	 out.writeObject("MESSAGE");
 	    	        	 out.writeObject(message);
 	    	        	 out.flush();
 	           		 } catch  (IOException e) {
 	   						e.printStackTrace();
 	   			     }
 	        	 }
        	 }
        }
        
        public void getAllClientDetails(int clientNum){
        	int i=0;
        	String message;
        	Set clientSet=clients.keySet();
        	for(i=0;i<clients.size();i++){
        			try {
        	        	out=clientOutputStreams.get(clientNum);
        	        	message="Client"+clientSet.toArray()[i]+" is connected";
						out.writeObject(message);
						out.flush();						
					} catch (IOException e) {
						e.printStackTrace();
					}	
        	}
        }
        
        public void broadCastFileToAll(int clientNum,String fileName,byte[]fileInBytes,int length){
        	String message="";
       	    Set set = clientOutputStreams.entrySet();
       	    Iterator it = set.iterator();
       	    while(it.hasNext()){
       		 Map.Entry pair = (Map.Entry)it.next();
	        	 message="@"+"client"+clientNum+":"+"file-"+fileName+" is received";
	        	 if((int)pair.getKey()!=clientNum){
	        		 try{
	    	        	 out=(ObjectOutputStream)pair.getValue();
	     	        	 out.writeObject("FILE");
	     	        	 out.writeObject(message);
	     	        	 //System.out.println(fileName);
	    	        	 out.writeObject(fileName);
	    	        	 out.writeInt(length);
	    	        	 out.writeObject("Client"+(int)pair.getKey());
	    	        	 out.write(fileInBytes,0,length);
	    	        	 out.flush();
	           		 } catch  (IOException e) {
	   						e.printStackTrace();
	   			     }
	        	 }
       	     }
        }
        
        public void broadCastFileToOne(int sender,int receiver,String fileName,byte fileInBytes[],int length){
        	String message="";
       	    Set set = clientOutputStreams.entrySet();
       	    Iterator it = set.iterator();
       	    while(it.hasNext()){
       		 Map.Entry pair = (Map.Entry)it.next();
	        	 message="@"+"client"+sender+":"+"file-"+fileName+" is received";
	        	 if((int)pair.getKey()!=sender && (int)pair.getKey()==receiver){
	        		 try{
	    	        	 out=(ObjectOutputStream)pair.getValue();
	     	        	 out.writeObject("FILE");
	     	        	 out.writeObject(message);
	     	        	 //System.out.println(fileName);
	    	        	 out.writeObject(fileName);
	    	        	 out.writeInt(length);
	    	        	 out.writeObject("Client"+(int)pair.getKey());
	    	        	 out.write(fileInBytes,0,length);
	    	        	 out.flush();
	           		 } catch  (IOException e) {
	   						e.printStackTrace();
	   			     }
	        	 }
       	     }
        }
        
        public void broadCastMessageToAllExceptOne(int sender,int nonReceiver){
        	Set set= clientOutputStreams.entrySet();
        	Iterator it=set.iterator();
        	String message;
        	while(it.hasNext()){
        		Map.Entry pair=(Map.Entry)it.next();
	        	message="@"+"client"+sender+":"+this.broadCastMessage;
                if((int)pair.getKey()!=sender && (int)pair.getKey()!=nonReceiver){
                	try{
                		out=(ObjectOutputStream)pair.getValue();
        	        	out.writeObject("MESSAGE");
                		out.writeObject(message);
 	    	        	out.flush();
                	} catch (IOException e) {
    					e.printStackTrace();
    				}
                }
        	}
        }
        
        public void broadCastFileToAllExceptOne(int sender,int nonReceiver,String fileName,byte fileInBytes[],int length){
        	String message="";
       	    Set set = clientOutputStreams.entrySet();
       	    Iterator it = set.iterator();
       	    while(it.hasNext()){
       		 Map.Entry pair = (Map.Entry)it.next();
	        	 message="@"+"client"+sender+":"+"file-"+fileName+" is received";
	        	 if((int)pair.getKey()!=sender && (int)pair.getKey()!=nonReceiver){
	        		 try{
	    	        	 out=(ObjectOutputStream)pair.getValue();
	     	        	 out.writeObject("FILE");
	     	        	 out.writeObject(message);
	    	        	 out.writeObject(fileName);
	    	        	 out.writeInt(length);
	    	        	 out.writeObject("Client"+(int)pair.getKey());
	    	        	 out.write(fileInBytes,0,length);
	    	        	 out.flush();
	           		 } catch  (IOException e) {
	   						e.printStackTrace();
	   			     }
	        	 }
       	     }
        }
                
    }
}