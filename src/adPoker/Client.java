package adPoker;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.DiffusionNumerotationPokerMessage;
import protocole.EjectionPokerMessage;
import protocole.PokerMessage;
import protocole.ReponseConnectionPokerMessage;
import reso.IClient;
import reso.IReso;

/**
 *
 * @author jeremy
 */
public class Client extends UnicastRemoteObject implements IClient {

    String nom; // Nom du joueur
    IReso reso;
    boolean enEcoute; // Permet d'ignorer les joueurs se connectant apres le boradcast d'une minute
    boolean ejection; // Permet d'ejecter un joueur qui se connecte trop tard
    List<String> adversaires;
    int id;

    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        ejection = false;
        reso = (IReso) Naming.lookup(IReso.NAME);
        enEcoute = true;
        adversaires = new ArrayList<>();
    }

    List<String> getAdversaires() {
        return adversaires;
    }

    void setEnEcoute(boolean enEcoute) {
        this.enEcoute = enEcoute;
    }

    boolean getEnEcoute() {
        return enEcoute;
    }

    int getId() {
        return this.id;
    }

    void setId(int id) {
        this.id = id;
    }

    // Retourne un nombre al?atoire de 0 au nombre d'aversaires
    int alea() {
        Random rand = new Random();
        return rand.nextInt(adversaires.size() - 0 + 1) + 0;
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);

        // Le message ne vient pas de nous
        if (!from.equals(nom)) {
            switch (pm.getType()) {

                // Un nouveau joueur nous envoie son nom
                case DIFFUSION_CONNECTION:
                    // la minute n'est pas ecoulee
                    if (enEcoute) {
                        adversaires.add(from);
                        ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                        reso.sendMessage(nom, from, msg2);
                        System.out.println(from + " a rejoint la partie");
                    } else {
                        // la minute est ecoulee, on ejecte le joueur
                        reso.sendMessage(nom, from, new EjectionPokerMessage());
                    }

                    break;

                // On recoit le lancement de l'election
                case DIFFUSION_NUMEROTATION:
                    enEcoute = false;
                    DiffusionNumerotationPokerMessage msg_temp = (DiffusionNumerotationPokerMessage) pm;
                    
                    if (adversaires.size() != msg_temp.getNbAdversaire()) {
                        System.out.println("Mise ? jour de la liste des adversaires");
                        adversaires.clear();
                        adversaires = msg_temp.getJoueursList();
                        System.out.println("MAJ ok");
                    }
                    
                    System.out.println(from + " On commence la numerotation");
                    
                    id = alea();
                    System.out.println("Mon Id : " + id);
                    break;

                case DIFFUSION_DEBUT_JEU:
                    break;

                // Un joueur a repondu suite a l'envoie de notre broadcast
                case REPONSE_CONNECTION:
                    adversaires.add(from);
                    ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                    System.out.println(msg_temp2.getNom() + " nous a accepte");
                    break;

                case DIFFUSION_EJECTION:
                    System.out.println("Vous arriver trop tard,\nLa partie a deja commence");
                    try {
                        Thread.sleep(10000);
                        System.exit(0);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }
    }
}
