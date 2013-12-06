import java.net.*; 
import java.io.*; 
import java.util.*;


public class peer{

	public static void main(String[] argv) throws IOException {
		if(argv.length!=3) 									
		// We need 1.server ip, 2.port number as a host and 3.a path for sharing in order 
		 	System.out.println("No enough parameters!");
		else{									// We need to do what peers should do
				Client my_client= new Client(argv); // initialization;
				if(my_client.try_join()==1){			// successfully join the P2P network
					System.out.println("Successfully joined the P2P network!");
					my_client.start();				// new thread, run function to handle requests outside
					my_client.in_request();				// handle with inside request;
					
					}
					
				
				else
					System.out.println("Fail to join P2P network, please check and try again.");
			} // end of joining P2P network;
			
		
	
	} //main
	
	
	
	
} //class peer

