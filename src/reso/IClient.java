package reso;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	
	void receiveMessage(String from, Serializable msg) throws RemoteException;
	
}
