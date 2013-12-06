
public class PeerInfo {
	public String Address;
	public int Port;
	
	public PeerInfo()
	{ //construct functinon
		Address="";
		Port=0;
	}
	
	
	public String getIP()
	{
		return Address;
	}
	
	public int getPort()
	{
		return Port;		
	}

	public void setIP(String ip)
	{
		Address = ip;
	}
	
	public void setPort(int number)
	{
		Port = number;		
	}
}
