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
import protocole.DiffusionFinNumerotationPokerMessage;
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

    private String nom; // Nom du joueur
    private IReso reso;
    private boolean enEcoute; // Permet d'ignorer les joueurs se connectant apres le boradcast d'une minute
    private boolean ejection; // Permet d'ejecter un joueur qui se connecte trop tard
    private List<Adversaire> adversaires;
    private int id;
    private int numerotationOk;
    private int numerotationTerminee;
    private int nbConflit;
    private Adversaire adversaireSuivant;

    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        ejection = false;
        reso = (IReso) Naming.lookup(IReso.NAME);
        enEcoute = true;
        adversaires = new ArrayList<>();
        id = -1;
        numerotationOk = 0;
        numerotationTerminee = 0;
    }

    List<Adversaire> getAdversaires() {
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
        for (Adversaire joueur : adversaires) {
            if (joueur.getNom().compareTo(nomJoueur) == 0) {
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
                        adversaires.add(new Adversaire(from));
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
                    for (Adversaire joueur : adversaires) {//Affiche la liste des joueurs
                        System.out.println("!!!" + joueur.getNom());
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
                        reso.broadcastMessage(nom, new DiffusionFinNumerotationPokerMessage(id));
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
                    adversaires.add(new Adversaire(from));
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
                break;
            case DIFFUSION_FIN_NUMEROTATION:
                DiffusionFinNumerotationPokerMessage msgFinNumerotation_temp = (DiffusionFinNumerotationPokerMessage) pm;
                numerotationTerminee++;
                for (Adversaire adv : adversaires) {
                    if (adv.getNom().compareTo(from) == 0) {
                        System.out.println(adv.getNom() + "=>" + msgFinNumerotation_temp.getId());
                        adv.setId(msgFinNumerotation_temp.getId()); //attributuion de l'ID

                    }
                }
                for (Adversaire adv : adversaires) {
                    if ((id + 1)% adversaires.size()  == adv.getId()) {
                        adversaireSuivant = adv;//cr?ation de l'anneau
                    }
                }
                if (numerotationTerminee == adversaires.size()) {
                    System.out.println("::Numerotation Terminee !!!:::" + numerotationTerminee);
                    System.out.println("Pr?t pour l'election, mon adv suivant est" + adversaireSuivant.getNom());
                } else {
                    System.out.println("::Numerotation PAS Terminee !!!:::" + numerotationTerminee);
                }
                break;
        }
        //}
    }
}
