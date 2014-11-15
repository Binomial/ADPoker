package reso;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface IReso extends Remote {
    
    public static final String NAME = "Reso";
    
    void declareClient(String Name, IClient client) throws RemoteException;
    void removeClient(String Name) throws RemoteException;
    void resetClients() throws RemoteException;
    
    void sendMessage(String from, String  to, Serializable msg) throws RemoteException;

    void broadcastMessage(String from, Serializable msg) throws RemoteException;

}
