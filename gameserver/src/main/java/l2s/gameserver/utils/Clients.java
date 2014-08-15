package l2s.gameserver.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clients 
{
	private static final Logger _log = LoggerFactory.getLogger(Clients.class);
	
	private static List<String> _knownIps = new ArrayList<String>();
	private static String client0 = "91.204.122.80";  // l2gold
	private static String client1 = "85.186.103.90";  // lineage2media
	private static String client2 = "112.213.95.87"; // l2vina
	private static String client3 = "118.69.174.4"; // nemchua
	private static String client4 = "127.0.0.1"; // 
	private static String client5 = "127.0.0.1"; // 
	private static String client6 = "127.0.0.1"; // 
	private static String client7 = "127.0.0.1"; // 
	private static String client8 = "127.0.0.1"; //
	private static String client9 = "80.71.255.29"; // Bonux server
	private static String client10 = "127.0.0.1"; // 
	private static String client11 = "127.0.0.1"; // 
	private static String client12 = "127.0.0.1"; //
	private static String client13 = "94.198.109.166"; // 166	
	
	public static void Load()
	{
		_knownIps.add(client0);
		_knownIps.add(client1);
		_knownIps.add(client2);
		_knownIps.add(client3);
		_knownIps.add(client4);
		_knownIps.add(client5);
		_knownIps.add(client6);
		_knownIps.add(client7);
		_knownIps.add(client8);
		_knownIps.add(client9);
		_knownIps.add(client10);
		_knownIps.add(client11);
		_knownIps.add(client12);
		_knownIps.add(client13);			
	}
	
	public static List<String> getIps()
	{
		return _knownIps;
	}
}
