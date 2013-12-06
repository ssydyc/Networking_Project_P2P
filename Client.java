import java.net.*; 
import java.io.*; 
import java.util.*;


public class Client extends Thread{ // we need two threads to do client jobs
	
	public String filePath;		// third parameter to be a file folder path
	public int serverPort;				//default one
	public String serverIP;
	public int myPort; 						//Port as a host
	public ArrayList<PeerInfo> Neighbours=new ArrayList<PeerInfo>(); 	// Neighbours information stored for quitting
	public boolean stop=false;							//We need something to stop out_request when in_request 
	public ArrayList<String> filelist= new ArrayList<String>();
	public Client(String[] argv){			//constructor
			filePath= argv[2];		// third parameter to be a file folder path
			serverPort=8888;				//default one
			serverIP= argv[0];
			myPort=Integer.parseInt(argv[1]); 						//Port as a host	
			stop=false;
			File folder=new File(filePath);
			File[] listofFiles=folder.listFiles();
			for(int i=0;i<listofFiles.length;i++){
				if (listofFiles[i].isFile()){
						filelist.add(listofFiles[i].getName());			// add file names to filename list
						
				} //end of if
			
			}//end of for
		} // end of constructor
	
	
	
	public int Confirmed(PeerInfo temp_peer){
		/**Test whether we can connect to peer host and	also notify him that you are connecting		*/
		try{
			Socket connection=new Socket(temp_peer.Address,temp_peer.Port);
			String request="join"+" "+Integer.toString(myPort); // send Join+Port to peer host
			InputStream in=connection.getInputStream(); 
			OutputStream out=connection.getOutputStream();
			out.write(request.getBytes());
			for(int i=0;i<500;i++){										// we wait for some time for success, then return 0
				byte[] data= new byte[1000]; 								//read 1000 bytes for one time
				in.read(data);
				String answer=new String(data).trim();
				if(answer.equals("success")){
					System.out.println("Join P2P network successfully!");
					connection.close();
				return 1;										// get success and thus return 1
			}
			Thread.sleep(10);
		
			}// end of for
		}catch (UnknownHostException e){  return 0;} 
		catch (IOException e) {return 0; }
		catch(InterruptedException ex) {
    	Thread.currentThread().interrupt();}
		return 0;
	} //end of constructor
	
	
	
	
	public int try_join(){
		/** Try to join the P2P network, if joined successfully, return 1 otherwise reture 0	*/
		try{	
				Socket connection=new Socket(serverIP,serverPort);
				String request="join"+" "+Integer.toString(myPort); // send Join+Port to server
				InputStream in=connection.getInputStream(); 
				OutputStream out=connection.getOutputStream();
				out.write(request.getBytes());				// format is "Join 8888"				
				
		while(true){					//waiting for server to respond
				byte[] data= new byte[1000]; 								//read 1000 bytes for one time
				in.read(data);
				String[] sdata= (new String(data)).trim().split(" ",0); 
				if(sdata[0].equals("onlyyou")){ 		//we are the only one in the P2P network
					connection.close();					//disconnect with server
					return 1;							
				}
				
				else if(sdata.length==2){			// If we have something in sdata, then it will be information of a host
					PeerInfo temp_peer=new PeerInfo();	
					temp_peer.setIP(sdata[0]);
					temp_peer.setPort(Integer.parseInt(sdata[1])); //store sdata to temp_peer for storing it
					connection.close();					//store our neighbours information and we can end
					if(this.Confirmed(temp_peer)==1){							// Join the P2P network of another peer
						Neighbours.add(temp_peer);
						System.out.println("Our neighbours in P2P network:");
						my_function.printPeers(Neighbours);
						return 1;
					}
					else return 0;
				}//end of else and if
				
		}// end of while
			}catch (UnknownHostException e){  return 0;} 
			catch (IOException e) {return 0; }			
	
		}// end of join
	
	
	
	
	public void run(){
	/** We will handle with outside requests in this thread, include share, quit, get,join */
	//format:quit port, 
		try{
			ServerSocket servSock=new ServerSocket(myPort);  		//create socket binding his own Port
			while(!stop){	//stop when quited
				if(stop) break;
				Socket connection= servSock.accept();				// receive requests
				if(stop) break;									//stop when we need to stop
				String clientAddress=connection.getRemoteSocketAddress().toString();	 //get client IP_address
				PeerInfo temp_peer=new PeerInfo();										//Store current IP and Port information
				temp_peer=my_function.convert_IPPort(clientAddress);
				System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"get connected!");
				InputStream in=connection.getInputStream(); 
			
				while(true){
					byte[] data= new byte[1000]; 								//read 1000 bytes for one time
					in.read(data);
					String tempdata=new String(data);
					String[] sdata=tempdata.trim().split(" ",0);				// handle with different requests 
					if(sdata.length==0)										//no data then break
						break;
					else if(sdata[0].equals("join")){					// format: join+ port number
						this.out_join(Integer.parseInt(sdata[1]),temp_peer,connection);		// send temp_peer and port number, notice port number is client's
						break;
					}
					else if(sdata[0].equals("quit")){
						this.out_quit(Integer.parseInt(sdata[1]),temp_peer);		//port number and peer information, notice port number in the temp_peer is useless
						break;
					}
					else if(sdata[0].equals("share")){					// format: share filename
						this.out_share(sdata[1],connection);
						break;
					}
					else if(sdata[0].equals("get")){					// format: get filename IP port flooding_time
						if(sdata.length==4){							// we don't have ip get filename port flooding_time
							this.out_get(sdata[1],temp_peer.Address,Integer.parseInt(sdata[2]),Integer.parseInt(sdata[3])+1);
						}
						else {this.out_get(sdata[1],sdata[2],Integer.parseInt(sdata[3]),Integer.parseInt(sdata[4])+1);}  // add one flooding_time				
						break;
					}
					else if(sdata[0].equals("stop")){
						break;
					}
					
			
				}// end of while for data
				connection.close();	
			}//end of first while for socket connection
			servSock.close();			//end of listening		
		}catch (UnknownHostException e){} 
		catch (IOException e) {}		
	}//end of run 
	
	
	public void out_join(int Portnumber,PeerInfo temp_peer,Socket connection) throws IOException{
	/** This function is to handle with the information when a peer wants to join you */
		temp_peer.Port=Portnumber;								// we want the listening port of our peers!
		System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"joins you!");
		Neighbours.add(temp_peer);								//store the peer that joins us 
		System.out.println("Our neighbours in P2P network:");
		my_function.printPeers(Neighbours);
		OutputStream out=connection.getOutputStream();
		out.write("success".getBytes());					//send success to confirm that they joined us successfully 
	}//end of our_join
	
	
	public void out_quit(int Portnumber, PeerInfo temp_peer){
	/** when one peer wants to leave, remove him */
		temp_peer.Port=Portnumber;		
		my_function.remove(temp_peer,Neighbours); //remove the neighbours
		System.out.println(temp_peer.Address+" "+Integer.toString(temp_peer.Port)+" "+"quits!");
		System.out.println("Our neighbours in P2P network:");
		my_function.printPeers(Neighbours);
	}// end of our_quit
	
	
	public void out_share(String filename,Socket connection) throws IOException{
	/** A peer wants to share files to you, we need to do three things, 1.informs him
	that we are ready, 2.download the file, 3.update our filelist */
		OutputStream out1=connection.getOutputStream();	
		if(filelist.contains(filename)){				//we already get this file
			out1.write("existed".getBytes());			// send existed to inform the peer
		}
		else{
			filelist.add(filename);						// update filelist
			out1.write("ready".getBytes());				//send ready to receive file
			System.out.println("receiving file: "+filename);
			String filename_path=filePath+"/"+filename;
			int count;
			FileOutputStream fos = new FileOutputStream(filename_path);
			BufferedOutputStream out = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			InputStream in = connection.getInputStream();
			while((count=in.read(buffer))>0){			//no data, then we have finishend
  		  		fos.write(buffer,0,count);
			}
		fos.close();					// close file
		System.out.println("file received!");	
		}
		
	}// end of out_share
	
	public void out_get(String filename, String IPaddress, int Portnumber, int flood_times)throws IOException{
	/** format: get filename IP port flooding_times, if we have this file, then send it to peer, otherwise
	send requests to all our neighbours*/
		if(flood_times>10) ;
		else if(filelist.contains(filename)){		// sending the file if we have
			System.out.println("We have the file "+filename+" requested!");
			in_share(filename,IPaddress,Portnumber);	
		}
		else{ //flooding to all neighours
			System.out.println("We don't have the file "+filename+" requested and will forward to my neighbours!");
			String request="get"+" "+filename+" "+IPaddress+" "+Integer.toString(Portnumber)+" "+Integer.toString(flood_times+1);
			for(PeerInfo temp_peer:Neighbours){
				my_function.send_info(temp_peer,request);
			}// end of for
		
		}// if condition ends
	
	
	}//end of out_get
	
	
	
	public void in_request() throws IOException{
		/**	Handle with the requests inside, including share, quit, get, list	*/
		while(true){
		 	Scanner input = new Scanner(System.in);
			System.out.println("Input a command: quit,list,share filename IP Port,get filename");
			String srequest=input.nextLine();
			String[] request= srequest.split(" ",0);
			if(request.length==0) ;					// not data received
			else if(request[0].equals("list")) 
				this.in_list();
			else if(request[0].equals("get")){
				if(request.length==2)
					this.in_get(request[1]);					//notice that get filename
				else
					System.out.println("Wrong number of parameters, should be: get filename!"); 
			}
			
			else if(request[0].equals("quit")){ 
				this.in_quit();
				stop=true;							// we need kill thread run
				PeerInfo temp_peer=new PeerInfo();
				temp_peer.Address=serverIP;
				temp_peer.Port=serverPort;
				my_function.send_info(temp_peer,"stop"+" "+Integer.toString(myPort)); //send stop to server to stop us
				break; // in_request stop
			}
			else if(request[0].equals("share")){
				if(request.length==4)
					this.in_share(request[1],request[2],Integer.parseInt(request[3])); // share filename IP Port 
				else
					System.out.println("Wrong number of parameters, should be: share filename IP Port!"); 
			}
		}// end of while		
	
	}// end of in_request
	
	
	public void in_list(){
		/** list all files in the filelist string array*/
		for(int i=0; i<filelist.size();i++){
			System.out.println(Integer.toString(i+1)+": "+filelist.get(i));
		}
	} //end of in_list
	

	
	public void in_quit() {
	/**The idea here is to send quit information to all neighbours and central-server */
	
		String request="quit"+" "+Integer.toString(myPort);
		PeerInfo server_peer=new PeerInfo();
		server_peer.Address=serverIP;
		server_peer.Port=serverPort;
		my_function.send_info(server_peer,request);					//quit server, send_quit is defined in my_function
		System.out.println("Successfully disconnected with central server!");
		for (PeerInfo temp_peer : Neighbours) {
				my_function.send_info(temp_peer,request);
				System.out.println("Successfully disconnected with "+temp_peer.Address+" "+Integer.toString(temp_peer.Port));
		}// end of for
		System.out.println("Successfully quits P2P network!");
		System.exit(0);
	}// end of quit function
	
	
	
	public void in_share(String filename, String IPaddress,int Portnumber) throws IOException{
	/** share a file to a peer*/
		if(filelist.contains(filename)){
			String request="share"+" "+filename;
			Socket connection=new Socket(IPaddress,Portnumber); 
			OutputStream out=connection.getOutputStream();
			InputStream tempin=connection.getInputStream();
			out.write(request.getBytes());		//send requet to the peer to share file
			byte[] data= new byte[1000];
			String sdata="";
			while(sdata.equals("")){
			tempin.read(data);
			sdata=new String(data).trim();	//wait until we get data
			}
			
			
			if(sdata.equals("ready")){ // ready to send out data
				String filename_path=filePath+"/"+filename;
				File myFile=new File(filename_path);
				System.out.println("Transferring file: "+filename);
				byte[] buffer=new byte[1024];
				int count;
				BufferedInputStream in =new BufferedInputStream(new FileInputStream(myFile));
				while((count=in.read(buffer))>=0){
					out.write(buffer,0,count);
					out.flush();
				}//end of while
				System.out.println("File transfer completed!");
			}// end of sending data
			else if(sdata.equals("existed")){ 		// the peer already got this file
				System.out.println("The peer already had this file!");
			
			}
			connection.close();
		}
		else
			System.out.println("No such file found in share folder, try another one");
	}//end of share
	
	
	public void in_get(String filename){
	/** trying to use flooding mechanism to get a file, here we just need to send requests to all
	out neighbours. */
	
		String request="get"+" "+filename+" "+Integer.toString(myPort)+" "+"0";
		for (PeerInfo temp_peer : Neighbours) {
			my_function.send_info(temp_peer,request); // send_quit can also send other kinds of data to peers
			System.out.println("sent request to "+temp_peer.Address+" "+Integer.toString(temp_peer.Port));
		}// end of for
		System.out.println("sent request to all neighbours!");
		test_get(filename);					//This function to test wether we get the file
			
	}//end of get
	
	public void test_get(String filename){
	/** print out time out information if we can't get the file*/
		try {
			for(int i=1;i<10;i++){
				Thread.sleep(500);
				if(filelist.contains(filename)) break;
			}
		} catch(InterruptedException ex) {
    	Thread.currentThread().interrupt();}
    	
		if(!filelist.contains(filename)){
		System.out.println("Time-out,can't get the file: "+filename);
		}
	}//end of test_get
	
} //class Client 
	
	
	
	
