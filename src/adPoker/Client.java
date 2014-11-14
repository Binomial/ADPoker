package adPoker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import jeuCarte.Carte;
import jeuCarte.JeuCartes;
import protocole.numerotation.DiffusionDebutNumerotationPokerMessage;
import protocole.numerotation.DiffusionFinNumerotationPokerMessage;
import protocole.election.DiffusionMaitre;
import protocole.numerotation.DiffusionNumerotationPokerMessage;
import protocole.connection.EjectionPokerMessage;
import protocole.election.ElectionPokerMessage;
import protocole.PokerMessage;
import protocole.affiche.AfficheCartePokerMessage;
import protocole.connection.ReponseConnectionPokerMessage;
import protocole.distribution.DistributionPokerMessage;
import protocole.echange.EchangePokerMessage;
import protocole.echange.InitEchangePokerMessage;
import protocole.echange.ReponseEchangePokerMessage;
import protocole.echange.ReponseMaitrePokerMessage;
import protocole.echange.TypeEchange;
import protocole.numerotation.ReponseNumerotationPokerMessage;
import protocole.numerotation.TypeReponseNumerotation;
import reso.IClient;
import reso.IReso;

public class Client extends UnicastRemoteObject implements IClient {

    private String nom; // Nom du joueur
    private IReso reso;
    private boolean enEcoute; // Permet d'ignorer les joueurs se connectant apres le boradcast d'une minute
    private boolean ejection; // Permet d'ejecter un joueur qui se connecte trop tard
    private List<Joueur> listJoueurs;
    private List<Carte> cartes;
    JeuCartes jeu;
    //numerotation
    private int id;
    private int numerotationOk;
    private int numerotationTerminee;
    private int nbConflit;
    private Joueur adversaireSuivant;
    //election
    private int numeroPlusFort;
    private Joueur maitre;
    //echange
    private int nbCarteAChanger;
    private int nbTour;
    public static final int NB_TOUR_MAX = 3;

    private File loger;
    private FileWriter ffw;
    
    Client(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        super();
        this.nom = nom;
        ejection = false;
        reso = (IReso) Naming.lookup(IReso.NAME);
        enEcoute = true;
        listJoueurs = new ArrayList<>();
        cartes = new ArrayList<>();
        id = -1;
        numerotationOk = 0;
        numerotationTerminee = 0;
        nbTour = 0;
        
       try {
            this.loger = new File("ADPoker/loger/log"+nom+"Msg.log");
            this.loger.createNewFile();
            this.ffw = new FileWriter(loger);
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    List<Joueur> getAdversaires() {
        return listJoueurs;
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
    int alea(int min, int max) {
        Random rand = new Random();
        // return rand.nextInt(adversaires.size() - 0 + 1) + 0;
        return rand.nextInt(max - min + 1) + min;
    }

    private boolean existeJoueur(String nomJoueur) {
        for (Joueur joueur : listJoueurs) {
            if (joueur.getNom().compareTo(nomJoueur) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        try {
            PokerMessage pm = ((PokerMessage) msg);
            // Le message ne vient pas de nous
            // if (!from.equals(nom)) {
            switch (pm.getType()) {
                // Un nouveau joueur nous envoie son nom
                case DIFFUSION_CONNECTION:
                    
                    ffw.write("****************\n");
                    ffw.write("Message de connexion d'un joueur\n");
                     //la minute n'est pas ecoulee
                    if (enEcoute) {
                        ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                        if (!existeJoueur(from)) {
                            listJoueurs.add(new Joueur(from));
                        }
                        System.out.println(from + " a rejoint la partie");
                        ffw.write( from + " a rejoint la partie\n");
                        reso.sendMessage(nom, from, msg2);
                        ffw.write("on répond avec ReponsePokerMessage | " + nom + "\n");
                        
                        
                    } else {
                        ffw.write("On ejecte " + from + "\n");
                        // la minute est ecoulee, on ejecte le joueur
                        reso.sendMessage(nom, from, new EjectionPokerMessage());
                    }
                    
                    ffw.flush();
                    break;
                    
                    // On recoit le lancement de la numerotation
                case DIFFUSION_NUMEROTATION:
                    enEcoute = false;
                    if (id == -1) {
                        for (Joueur joueur : listJoueurs) {//Affiche la liste des joueurs
                            System.out.println("!!!" + joueur.getNom());
                        }
                        DiffusionDebutNumerotationPokerMessage msg_temp = (DiffusionDebutNumerotationPokerMessage) pm;
                        if (listJoueurs.size() != msg_temp.getNbAdversaire()) {
                            System.out.println("Mise a jour de la liste des adversaires");
                            listJoueurs = msg_temp.getJoueursList();
                            System.out.println("MAJ ok");
                        }
                        nbConflit = listJoueurs.size() - 1;
                        System.out.println(from + " On commence la numerotation");
                        
                        id = alea(0, listJoueurs.size() - 1);
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
                        id = alea(0, listJoueurs.size() - 1);
                        System.out.println("Mon nouvel Id : " + id);
                        DiffusionNumerotationPokerMessage newNumerotationMsg = new DiffusionNumerotationPokerMessage(id, nom, false);
                        reso.broadcastMessage(from, newNumerotationMsg);
                        nbConflit = nbConflit + (listJoueurs.size() - 2);
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
                        listJoueurs.add(new Joueur(from));
                        ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                        System.out.println(msg_temp2.getNom() + " nous a accepte");
                    }
                    break;
                    
                case DIFFUSION_EJECTION:
                    System.out.println("Vous arriver trop tard,\nLa partie a deja commence");
                    try {
                        Thread.sleep(10000);
                        //System.exit(0);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case DIFFUSION_FIN_NUMEROTATION:
                    DiffusionFinNumerotationPokerMessage msgFinNumerotation_temp = (DiffusionFinNumerotationPokerMessage) pm;
                    numerotationTerminee++;
                    for (Joueur adv : listJoueurs) {
                        if (adv.getNom().compareTo(from) == 0) {
                            System.out.println(adv.getNom() + "=>" + msgFinNumerotation_temp.getId());
                            adv.setId(msgFinNumerotation_temp.getId()); //attributuion de l'ID
                        }
                    }
                    for (Joueur adv : listJoueurs) {
                        if ((id + 1) % listJoueurs.size() == adv.getId()) {
                            adversaireSuivant = adv;//cr?ation de l'anneau
                        }
                    }
                    if (numerotationTerminee == listJoueurs.size()) {
                        
                        System.out.println("::Numerotation Terminee !!!:::" + numerotationTerminee);
                        System.out.println("Pr?t pour l'election, mon adv suivant est" + adversaireSuivant.getNom());
                        ElectionPokerMessage message = new ElectionPokerMessage(id, id);
                        numeroPlusFort = id;
                        System.out.println("DEBUT ELECTION");
                        reso.sendMessage(nom, adversaireSuivant.getNom(), message);
                        
                    } else {
                        System.out.println("::Numerotation PAS Terminee !!!:::" + numerotationTerminee);
                    }
                    break;
                    
                case MESSAGE_ELECTION:
                    ElectionPokerMessage msgElection_temp = (ElectionPokerMessage) pm;
                    System.out.println("numLePlusFort : " + numeroPlusFort + "idRecu" + msgElection_temp.getNumeroPlusFort());
                    if (msgElection_temp.getNumeroPlusFort() > numeroPlusFort) {
                        numeroPlusFort = msgElection_temp.getNumeroPlusFort();
                        reso.sendMessage(nom, adversaireSuivant.getNom(), new ElectionPokerMessage(msgElection_temp.getId(), numeroPlusFort));
                        System.out.println("numleplusfort : " + numeroPlusFort);
                    } else if (msgElection_temp.getNumeroPlusFort() < numeroPlusFort) {
                        reso.sendMessage(nom, adversaireSuivant.getNom(), new ElectionPokerMessage(msgElection_temp.getId(), numeroPlusFort));
                    } else if (msgElection_temp.getNumeroPlusFort() == id && msgElection_temp.getId() == id) {
                        System.out.println("broadcast maitre");
                        jeu = new JeuCartes();
                        reso.broadcastMessage(nom, new DiffusionMaitre(nom));
                        for (int i = 0; i < 5; i++) {
                            for (Joueur adve : listJoueurs) {
                                DistributionPokerMessage distributionMessage = new DistributionPokerMessage(jeu.nvlleCarte());
                                reso.sendMessage(nom, adve.getNom(), distributionMessage);
                            }
                        }
                    }
                    System.out.println("Envoi du message d'init echange");
                    reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                    System.out.println("FIN Envoi du message d'init echange");
                    
                    break;
                case DIFFUSION_MAITRE:
                    DiffusionMaitre msgMaitre_temp = (DiffusionMaitre) pm;
                    maitre = msgMaitre_temp.getMaitre();
                    System.out.println("maitre : " + msgMaitre_temp.getMaitre().getNom());
                    break;
                    
                case MESSAGE_DISTRIBUTION:
                    DistributionPokerMessage msgDistribution_temp = (DistributionPokerMessage) pm;
                    cartes.add(msgDistribution_temp.getCarte());
                    System.out.println(msgDistribution_temp.getCarte());
                    if (cartes.size() == 5) {
                        System.out.println("Cartes recus, on attend notr tour pour echanger");
                        
                        if (from.compareTo(nom) == 0) {
                            System.out.println("Envoi du message d'init echange");
                            reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                            System.out.println("FIN Envoi du message d'init echange");
                            
                        }
                    }
                    
                    break;
                    
                case REPONSE_AU_MAITRE:
                    ReponseMaitrePokerMessage msgReponseMaitre_temp = (ReponseMaitrePokerMessage) pm;
                    System.out.println("Carte recu de l echange : " + msgReponseMaitre_temp.getCarte());
                    jeu.ajoutCarte(msgReponseMaitre_temp.getCarte());
                    Carte nouvelleCarte = jeu.nvlleCarte();
                    ReponseEchangePokerMessage msgKriss = new ReponseEchangePokerMessage(nouvelleCarte);
                    reso.sendMessage(nom, from, msgKriss);
                    System.out.println("Carte envoye " + nouvelleCarte);
                    break;
                    
                case MESSAGE_ECHANGE:
                    EchangePokerMessage msgEchange_temp = (EchangePokerMessage) pm;
                    System.out.println("ordinal message" + msgEchange_temp.getTypeEchange() + " ordinal enum" + TypeEchange.ECHANGE);
                    if (msgEchange_temp.getTypeEchange().ordinal() == TypeEchange.ECHANGE.ordinal()) {
                        System.out.println("ECHANGE");
                        ReponseEchangePokerMessage msg_EchangeCarte = (ReponseEchangePokerMessage) msgEchange_temp;
                        cartes.add(msg_EchangeCarte.getCarte());
                        System.out.println("Nouvelle carte : " + msg_EchangeCarte.getCarte());
                    } else {
                        System.out.println("INIT");
                        nbCarteAChanger = alea(2, 5);
                        System.out.println("Carte a echangee : " + nbCarteAChanger);
                    }
                    if (nbCarteAChanger > 0) {
                        ReponseMaitrePokerMessage reponseEchange = new ReponseMaitrePokerMessage(cartes.remove(0));
                        System.out.println("Nom du maitre : " + maitre.getNom());
                        reso.sendMessage(nom, maitre.getNom(), reponseEchange);
                        nbCarteAChanger--;
                        System.out.println("Carte a echangee : " + nbCarteAChanger + "j'envoie : " + reponseEchange.getCarte());
                    } else {
                        System.out.println("passage de jeton a " + adversaireSuivant.getNom());
                        if (nom.compareTo(maitre.getNom()) == 0) {
                            nbTour++;
                            if (nbTour == NB_TOUR_MAX) {
                                System.out.println("Fin des echanges");
                                reso.sendMessage(nom, adversaireSuivant.getNom(), new AfficheCartePokerMessage());
                            } else {
                                System.out.println("Fin du tour " + nbTour);
                                reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                            }
                            
                        } else {
                            reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                        }
                    }
                    
                    break;
                case MESSAGE_AFFICHE:
                    System.out.println(nom + ":" + cartes.remove(0));
                    if (nom.compareTo(maitre.getNom()) == 0) {
                        
                    }
                    if (cartes.isEmpty()) {
                        System.out.println("FIN DE LA PARTIE");
                        //exit
                    } else {
                        reso.sendMessage(nom, adversaireSuivant.getNom(), new AfficheCartePokerMessage());
                    }
                    
                    break;
                    
            }
            
            //}
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
