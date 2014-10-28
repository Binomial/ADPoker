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
    int nbFinChronoRecu; // Permet d'ignorer les joueurs se connectant apres le boradcast d'une minute
    List<String> adversaires;
    int id;

    boolean testDer;
    
    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        this.reso = (IReso) Naming.lookup(IReso.NAME);
        this.nbFinChronoRecu = 0;
        this.adversaires = new ArrayList<>();
        this.id = -1;
        
        this.testDer = true;
    }

    List<String> getAdversaires() {
        return adversaires;
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

                        testDer = false; // On est pas le dernier
                        adversaires.add(from);
                        ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                        reso.sendMessage(nom, from, msg2);
                        System.out.println(from + " a rejoint la partie");

                    break;

                // On recoit un lancement de l'election
                case DIFFUSION_ELECTION:
                    break;

                case DIFFUSION_DEBUT_JEU:
                    break;

                // Un joueur a repondu suite a l'envoie de notre broadcast
                case REPONSE_CONNECTION:
                    // si le nom est deja dans la liste nom connecter avant ou en meme temps
                    if(! adversaires.contains(nom)) {
                        adversaires.add(from);
                    }

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

    void setTest(boolean b) {
        this.testDer = b;
    }
    
    boolean getTest() {
        return testDer;
    }
}
