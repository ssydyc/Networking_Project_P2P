import java.net.*; 
import java.io.*; 
import java.util.*;


public class my_function{
	/** store all functions needed for other programs */
	
	
	public static PeerInfo convert_IPPort(String clientAddress){
	
	/** convert string in clinetAddress into IP and port, the format in clientAddress 
	like /127.0.0.1:88888 	*/
	
	String[] IPPort=clientAddress.split(":",0);
	PeerInfo temp_peer=new PeerInfo();
	IPPort[0]=IPPort[0].substring(1); 	// remove the first slash in the front
	temp_peer.setIP(IPPort[0]);
	temp_peer.setPort(Integer.parseInt(IPPort[1]));
	return temp_peer;
	
	}
	
	public static void printPeers(ArrayList<PeerInfo> Peers){
	
	/** print all the peers information in Peerlist */
	
	int i=0;
	for(i=0;i<Peers.size();i++){
		if(Peers.size()!=0){
		System.out.println(Integer.toString(i)+": "+Peers.get(i).Address+" "+Integer.toString(Peers.get(i).Port));
		}
		}
	}
	
	
	
	public static void send_info(PeerInfo temp_peer, String request){
	/**	send the neighbout temp_peer with string request		*/
	try{
			Socket connection=new Socket(temp_peer.Address,temp_peer.Port); 
			OutputStream out=connection.getOutputStream();
			out.write(request.getBytes());	
			connection.close();
		}
		catch (UnknownHostException e){} 
			catch (IOException e) {}	
	} //end of send_quit
	
	
	public static void remove(PeerInfo temp_peer, ArrayList<PeerInfo> Peers){
	
		for(int i=0;i<Peers.size();i++){
			if((Peers.get(i).Port==temp_peer.Port)&&(Peers.get(i).Address.equals(temp_peer.Address))){
				Peers.remove(i);
			}		
		
		}
	
	} //end of remove
	
	
} //my_function