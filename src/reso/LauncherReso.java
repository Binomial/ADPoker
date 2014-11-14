package reso;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class LauncherReso {

	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.createRegistry(IReso.PORT);
			
			IReso reso = new ResoImpl();
			registry.rebind(IReso.NAME, reso);
			
			System.out.println("Reso successfully launched!");
		} catch (RemoteException ex) {
		}

	}

}
