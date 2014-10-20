package reso;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import reso.IClient;
import reso.IReso;

public class ReceiverTemp extends UnicastRemoteObject implements IClient {

	private static final long serialVersionUID = 842940924381059064L;

	protected ReceiverTemp() throws RemoteException {
		super();
	}

	@Override
	public void receiveMessage(String from, Serializable msg)
			throws RemoteException {
		System.out.println("Receiver received a message from " + from + ": " + msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IReso reso = (IReso) Naming.lookup(IReso.NAME);
			
			String nameReceiver = new String("nRR");
			reso.declareClient(nameReceiver, new ReceiverTemp());
			System.out.println("Receiver with name " + nameReceiver);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
