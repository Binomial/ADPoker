package reso;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import reso.IClient;
import reso.IReso;

public class SenderTemp extends UnicastRemoteObject implements IClient {

	private static final long serialVersionUID = -3879530234484702428L;

	protected SenderTemp() throws RemoteException {
		super();
	}

	@Override
	public void receiveMessage(String from, Serializable msg)
			throws RemoteException {
		System.out.println("Sender received a message from " + from + ": " + msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IReso reso = (IReso) Naming.lookup(IReso.NAME);
			
			String nameSender = new String("nS");
			reso.declareClient(nameSender, new SenderTemp());
			System.out.println("Sender has name: " + nameSender);
			
			for (int i = 0; i < 10; i++) {
				reso.sendMessage(nameSender, "nR", "Hello you!");
			}
			
			reso.broadcastMessage("nR", "Diffusion");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
