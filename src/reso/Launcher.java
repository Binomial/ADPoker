package reso;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Launcher {

	public static void main(String[] args) {

		try {
			// Change address
			String myAddress="";
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()){
				List<InterfaceAddress> i = en.nextElement().getInterfaceAddresses();			
			    for (InterfaceAddress l : i) {
			    	InetAddress adr = l.getAddress();
			        if  (adr.isSiteLocalAddress()){
			           myAddress=adr.getHostAddress();
			        }
			    }
			}
			System.setProperty("java.rmi.server.hostname", myAddress);

			Registry registry = LocateRegistry.createRegistry(1099);
			
			IReso reso = new ResoImpl();
			registry.rebind(IReso.NAME, reso);
			
			System.out.println("Reso successfully launched!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
