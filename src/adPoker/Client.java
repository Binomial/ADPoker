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
import protocole.EjectionPokerMessage;
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
    boolean enEcoute; // Permet d'ignorer les joueurs se connectant apr?s le boradcast d'une minute
    boolean ejection = false;
    
    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        reso = (IReso) Naming.lookup(IReso.NAME);
        enEcoute = true;
    }

    void setEnEcoute(boolean enEcoute) {
        this.enEcoute = enEcoute;
    }
    
    boolean getEnEcoute() {
        return enEcoute;
    }
        
    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);
        
        // Si on recoit un message qui ne vient pas de nous
        if (!from.equals(nom)) {
            if (enEcoute && pm.getType() != TypeMessage.DIFFUSION_EJECTION)
                System.out.println("Message recu : " + pm.getType() + " de " + from);

            switch (pm.getType()) {

                // Un nouveau joueur nous envoie son nom
                case DIFFUSION_CONNECTION:
                    // Si on a pas re?ut de fin d'ecoute
                    if(enEcoute) {
                        DiffusionConnectionPokerMessage msg_temp = (DiffusionConnectionPokerMessage) pm;
                        // On donne notre nom au nouveau joueur
                        ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                        reso.sendMessage(nom, from, msg2);
                    }else{
                        // ESSAYER D'ENVOYER QU'UNE FOIS L'EJECTION POUR PAS MERDER LE SERVEUR
                        reso.sendMessage(nom, from, new EjectionPokerMessage());
                    }
                    
                    break;

                case DIFFUSION_ELECTION:
                    enEcoute = false;
                    System.out.println("On peut etablir notre numero");
                    break;
                // Le premier joueur a attendu 1 min, donc on peut etablir les numero pour l'election
                case DIFFUSION_DEBUT_JEU:
                    // Si on a pas re?ut de fin d'ecoute
                    if(enEcoute) {
                        System.out.println("On ne recoit plus de joueur, on etablit les numeros des connectes");
                        reso.broadcastMessage(nom, msg);
                    }
                    break;
                // Un joueur a repondu suite a l'envoie de notre broadcast
                case REPONSE_CONNECTION:
                    ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                    System.out.println(msg_temp2.getNom() + " a rejoint la liste d'adversaire");
                    break;
                    
                case DIFFUSION_EJECTION:
                    System.out.println("Oh chiotte ils veulent pas de moi");
                    System.exit(0);
            }
        }
    }
}
