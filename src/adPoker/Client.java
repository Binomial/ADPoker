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
import protocole.DiffusionFinAttentePokerMessage;
import protocole.DiffusionNumerotationPokerMessage;
import protocole.PokerMessage;
import protocole.ReponseConnectionPokerMessage;
import reso.IClient;
import reso.IReso;

/**
 * @author jeremy
 */
public class Client extends UnicastRemoteObject implements IClient {

    String nom; // Nom du joueur
    IReso reso;
    int nbFinChronoRecu; // Permet d'ignorer les joueurs se connectant apres le boradcast d'une minute
    List<String> listJoueurs;
    List<Integer> listNumDispo;
    int id, nbTour, nbMsgNumRecu;
    int advSansNum;

    boolean estPret;

    void afficheAdv() {
        for (String i : listJoueurs) {
            System.out.println(i);
        }
    }

    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        this.reso = (IReso) Naming.lookup(IReso.NAME);
        this.nbFinChronoRecu = 0;
        this.listJoueurs = new ArrayList<>();
        this.id = -1;
        this.nbTour = 0;
        this.estPret = true;
        this.nbMsgNumRecu = 0;
    }

    List<String> getAdversaires() {
        return listJoueurs;
    }

    int getId() {
        return this.id;
    }

    void setId(int id) {
        this.id = id;
    }

    // Retourne un nombre aleatoire de 0 au nombre d'aversaires
    int alea() {
        Random rand = new Random();
        int temp = rand.nextInt(listNumDispo.size() - 0 + 1) + 0;
        return listNumDispo.get(temp);
    }

    void initListeNum() {
        this.listNumDispo = new ArrayList<>();
        for (int i = 0; i < listJoueurs.size(); i++) {
            listNumDispo.add(i);
        }
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);

        // Le message ne vient pas de nous
        switch (pm.getType()) {

            // Un nouveau joueur nous envoie son nom
            case DIFFUSION_CONNECTION:

                listJoueurs.add(from);

                if (!nom.equals(from)) {
                    ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                    reso.sendMessage(nom, from, msg2);
                    System.out.println(from + " a rejoint la partie");
                }
                break;

            // Un joueur a repondu suite a l'envoie de notre broadcast
            case REPONSE_CONNECTION:
                // si le nom est deja dans la liste nom connecter avant ou en meme temps
                if (! listJoueurs.contains(from)) {
                    listJoueurs.add(from);
                    ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                    System.out.println(msg_temp2.getNom() + " nous a accepte");
                } else {
                    System.err.println(from + " s'est connecte en meme temps que nous");
                }
                break;

            // Un joueur a attendu 1 min
            case DIFFUSION_FIN_ATTENTE:
                System.out.println("On recoit une fin de "+ from);
                DiffusionFinAttentePokerMessage msgFin = (DiffusionFinAttentePokerMessage) msg;

                // Si sa liste d'adversaire est plus grande que la notre, on met a jour
                if (msgFin.getJoueursList().size() > listJoueurs.size()) {
                    System.out.println(from + " nous envoie sa liste");
                    listJoueurs = msgFin.getJoueursList();
                    System.out.println("Liste ok");
                }

                System.out.println("Notre liste de joueur : ");
                afficheAdv();

                advSansNum = listJoueurs.size();
                System.out.println(advSansNum + " joueur sans numero");

                id = alea();
                System.out.println("J'ai tire " + id);

                DiffusionNumerotationPokerMessage msgDiffS = new DiffusionNumerotationPokerMessage(id, nbTour);
                reso.broadcastMessage(nom, msgDiffS);
                System.out.println("Mon id est broadcaste");

                break;

            case DIFFUSION_NUM:
                if (!nom.equals(from)) {
                    nbMsgNumRecu++;

                    // Tout le monde a fait un broadcasr
                    if (nbMsgNumRecu == advSansNum) {
                        nbTour++;

                        DiffusionNumerotationPokerMessage msgDiff = (DiffusionNumerotationPokerMessage) msg;

                        //On est ok, on supprime nos num du tableau
                        if (msgDiff.getId() != id && msgDiff.getNumTour() == nbTour) {
                            advSansNum--;
                            for (int i = 0; i < listNumDispo.size(); i++) {
                                if (msgDiff.getId() == listNumDispo.get(i)) {
                                    listNumDispo.remove(i);
                                }
                            }
                            if (advSansNum == 0) {
                                // Fin
                            }
                        } else {
                            System.err.println("Pas bon pour moi " + nom + " et " + from + " id " + id);
                        }
                    }
                }
                break;

            case DIFFUSION_DEBUT_JEU:
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

    void setEstPret(boolean b) {
        this.estPret = b;
    }

}
