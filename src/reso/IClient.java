package reso;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	public static final String NAME = "Client";
        public static final int PORT = 1098;
        
	void receiveMessage(String from, Serializable msg) throws RemoteException;
	
}
