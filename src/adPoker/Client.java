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
import protocole.DiffusionDebutNumerotationPokerMessage;
import protocole.DiffusionNumerotationPokerMessage;
import protocole.EjectionPokerMessage;
import protocole.PokerMessage;
import protocole.ReponseConnectionPokerMessage;
import protocole.ReponseNumerotationPokerMessage;
import protocole.TypeReponseNumerotation;
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
    int numerotationOk;
    private int nbConflit;

    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        ejection = false;
        reso = (IReso) Naming.lookup(IReso.NAME);
        enEcoute = true;
        adversaires = new ArrayList<>();
        id = -1;
        numerotationOk = 0;
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

    public String getNom() {
        return nom;
    }

    // Retourne un nombre al?atoire de 0 au nombre d'aversaires
    int alea() {
        Random rand = new Random();
        // return rand.nextInt(adversaires.size() - 0 + 1) + 0;
        return rand.nextInt(adversaires.size() - 0 + 0) + 0;
    }

    private boolean existeJoueur(String nomJoueur) {
        for (String joueur : adversaires) {
            if (joueur.compareTo(nomJoueur) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);
        // Le message ne vient pas de nous
        // if (!from.equals(nom)) {
        switch (pm.getType()) {

            // Un nouveau joueur nous envoie son nom
            case DIFFUSION_CONNECTION:
                // la minute n'est pas ecoulee
                if (enEcoute) {

                    ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                    if (!existeJoueur(from)) {
                        adversaires.add(from);
                    }
                    reso.sendMessage(nom, from, msg2);
                    System.out.println(from + " a rejoint la partie");
                } else {
                    // la minute est ecoulee, on ejecte le joueur
                    reso.sendMessage(nom, from, new EjectionPokerMessage());
                }
                break;

            // On recoit le lancement de la numerotation
            case DIFFUSION_NUMEROTATION:
                enEcoute = false;
                if (id == -1) {
                    for (String joueur : adversaires) {//Affiche la liste des joueurs
                        System.out.println("!!!" + joueur);
                    }
                    DiffusionDebutNumerotationPokerMessage msg_temp = (DiffusionDebutNumerotationPokerMessage) pm;
                    if (adversaires.size() != msg_temp.getNbAdversaire()) {
                        System.out.println("Mise a jour de la liste des adversaires");
                        adversaires = msg_temp.getJoueursList();
                        System.out.println("MAJ ok");
                    }
                    nbConflit = adversaires.size() - 1;
                    System.out.println(from + " On commence la numerotation");

                    id = alea();
                    System.out.println("Mon Id : " + id);
                    System.out.println("Mon Nom : " + nom);
                    DiffusionNumerotationPokerMessage numerotationMsg = new DiffusionNumerotationPokerMessage(id, nom, true);
                    reso.broadcastMessage(from, numerotationMsg);
                }
                break;

            case MESSAGE_NUMEROTATION:

                DiffusionNumerotationPokerMessage msgNumerotation_temp = (DiffusionNumerotationPokerMessage) pm;
                System.out.println("ALLO" + msgNumerotation_temp.getNomSender());
                if ((msgNumerotation_temp.getNomSender().compareTo(nom) != 0)) { //|| (msgNumerotation_temp.getEcouterSonBroadcast())
                    ReponseNumerotationPokerMessage reponseNumerotationMsg;
                    if (id == msgNumerotation_temp.getNumero()) {
                        reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.CONFLIT);
                        System.err.println("CONFLIT sur le numero moi :" + id + "lui:" + msgNumerotation_temp.getNumero() + "avec le joueur : " + msgNumerotation_temp.getNomSender());

                    } else {
                        reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.OK);
                        System.out.println("Numero OK");
                    }
                    reso.sendMessage(from, msgNumerotation_temp.getNomSender(), reponseNumerotationMsg);
                }

                break;

            case REPONSE_NUMEROTATION:
                ReponseNumerotationPokerMessage msgReponseNumerotation_temp = (ReponseNumerotationPokerMessage) pm;
                System.out.println("REPONSE");
                if (msgReponseNumerotation_temp.getReponse() == TypeReponseNumerotation.CONFLIT) {
                    id = alea();
                    System.out.println("Mon nouvel Id : " + id);
                    DiffusionNumerotationPokerMessage newNumerotationMsg = new DiffusionNumerotationPokerMessage(id, nom, false);
                    reso.broadcastMessage(from, newNumerotationMsg);
                    nbConflit = nbConflit + (adversaires.size() - 2);
                } else if (msgReponseNumerotation_temp.getReponse() == TypeReponseNumerotation.OK) {
                    numerotationOk++;
                    if (numerotationOk == nbConflit) {
                        System.out.println("Numerotation finie" + (numerotationOk) + "/" + nbConflit);
                    } else {
                        System.out.println("Numerotation pas finie" + (numerotationOk) + "/" + nbConflit);
                    }
                }
                break;

            case DIFFUSION_DEBUT_JEU:
                break;

            // Un joueur a repondu suite a l'envoie de notre broadcast
            case REPONSE_CONNECTION:
                if (!existeJoueur(from)) {
                    adversaires.add(from);
                    ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                    System.out.println(msg_temp2.getNom() + " nous a accepte");
                }
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
        //}
    }
}
