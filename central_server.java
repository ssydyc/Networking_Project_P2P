import java.net.*; 
import java.io.*; 
import java.util.*;


public class central_server{

/** central_server program to give host information */

	public static void main(String[] args) throws IOException{
		int servPort=8888;															//define the server port 8888
		ServerSocket servSock=new ServerSocket(servPort); 							// bind the port
		ArrayList<PeerInfo> Peers= new ArrayList<PeerInfo>(); 						// Store peers' Information
		
		while(true){
			Socket connection= servSock.accept();
			String clientAddress=connection.getRemoteSocketAddress().toString();	 //get client IP_address
			PeerInfo temp_peer=new PeerInfo();										//Store current IP and Port information
			temp_peer=my_function.convert_IPPort(clientAddress);
			System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"get connected!");	 // clientAddress has the form like /127.0.0.1:88888
			
			
			while(true){
																					// trying to get the command from client, otherwise waiting
				InputStream in=connection.getInputStream(); 
				OutputStream out=connection.getOutputStream();						 //in for receiving data and out for 
				byte[] data= new byte[1000]; 										//read 1000 bytes for one time
				in.read(data);
				String tempdata=new String(data);
				String[] sdata= tempdata.trim().split(" ",0); 						//sdata is the data of string type
				if(sdata[0].equals("join")){									// data type received should be like "Join 8888"
					temp_peer.Port=Integer.parseInt(sdata[1]);					// port value received from client
					System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"is joining P2P network!");
					// If no other peers, then no information, else randomly picks up a peer.
					if ((Peers.size())==0){ 									// Send onlyyou back to client to let him be host
							Peers.add(temp_peer);
							out.write("onlyyou".getBytes());	//send onlyyou to client
							}
					else{  									//randomly pick up a client in the peerlist
							if(!Peers.contains(temp_peer)){	// He is not in the peerlist
								Peers.add(temp_peer);
								Random generator = new Random();
								int choosen=0;
								if (Peers.size()==2) {choosen=0;}
								else {choosen = generator.nextInt(Peers.size()-2);}	// Generate a number between 0 and Peers.size()-2
								String send=Peers.get(choosen).Address+" "+Integer.toString(Peers.get(choosen).Port);
								out.write(send.getBytes());
							
								}
							
							else
								out.write("alreadyjoined".getBytes()); 			// already joined in this case 
					
						} // end of registration
					
					System.out.println("Remaining peers in P2P network:");
					my_function.printPeers(Peers);
					break;
				}											
				else if(sdata[0].equals("quit")){						// request format is "quit+Port number" 
					temp_peer.Port=Integer.parseInt(sdata[1]);
					if(Peers.contains(temp_peer)) {System.out.println("contain!");}
					my_function.remove(temp_peer,Peers);
					System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"quited!");
					System.out.println("Remaining peers in P2P network:");
					my_function.printPeers(Peers);
					break;
				}
				else if(sdata.length==0){				//we can only have quit or join command, otherwise wrong and ignore it.
					break;
					}
				else if(sdata[0].equals("stop")){		// we want to stop a client's socket if he requests
					temp_peer.Port=Integer.parseInt(sdata[1]);
					my_function.send_info(temp_peer,"stop");
					break;
				}
			} //  then wait
			
			connection.close();
		} // while, always listen
			
	} //main
}