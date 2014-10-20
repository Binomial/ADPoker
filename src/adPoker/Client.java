/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adPoker;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import protocole.DiffusionConnectionPokerMessage;
import protocole.PokerMessage;
import protocole.ReponseConnectionPokerMessage;
import protocole.TypeMessage;
import reso.IClient;
import reso.IReso;

/**
 *
 * @author jeremy
 */
public class Client extends UnicastRemoteObject implements IClient {

    String nom; // Nom du joueur
    IReso reso;
    
    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        reso = (IReso) Naming.lookup(IReso.NAME);
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);
        
        // Si on recoit un message qui ne vient pas de nous
        if (!from.equals(nom)) {

            System.out.println("Message recu : " + pm.getType() + " de " + from);

            switch (pm.getType()) {

                // Un nouveau joueur nous envoie son nom
                case DIFFUSION_CONNECTION:
                    DiffusionConnectionPokerMessage msg_temp = (DiffusionConnectionPokerMessage) pm;
                    //On fait un send message ? from de nom
                    ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                    reso.sendMessage(nom, from, msg2);
                    break;

                // Le premier joueur a attendu 1min, donc on peut etablir les numero pour l'election
                case DIFFUSION_DEBUT_JEU:
                    System.out.println("On ne recoit plus de joueur, on etablit les numeros des connectes");
                    break;
                // Un joueur a repondu suite a l'envoie de notre broadcast
                case REPONSE_CONNECTION:
                    ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                    System.out.println(msg_temp2.getNom() + " a rejoint la liste d'adversaire");
                    break;
            }
        }
    }
}
