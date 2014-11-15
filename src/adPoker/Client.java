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
            this.loger = new File("loger/ReceptionMessages/log_"+nom+".log");
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
                    ffw.write("On reçoit DiffusionConnectionPokerMessage | " + from +"\n");
                    
                    
                     //la minute n'est pas ecoulee
                    if (enEcoute) {
                        ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                        if (!existeJoueur(from)) {
                            listJoueurs.add(new Joueur(from));
                        }
                        System.out.println(from + " a rejoint la partie");
                        ffw.write( from + " a rejoint la partie\n");
                        reso.sendMessage(nom, from, msg2);
                        ffw.write("On répond avec ReponsePokerMessage | " + nom + "\n");
                        
                        
                    } else {
                        ffw.write("On ejecte " + from + " avec EjectionPokerMessage\n");
                        // la minute est ecoulee, on ejecte le joueur
                        reso.sendMessage(nom, from, new EjectionPokerMessage());
                    }
                    break;
                    
                    // On recoit le lancement de la numerotation
                case DIFFUSION_NUMEROTATION:
                    ffw.write("****************\n");
                    ffw.write("Préparation et lancement de la numérotation\n");
                    ffw.write(("On reçoit DiffusionDebutNumerotationPokerMessage | liste des joueurs\n"));
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
                        ffw.write("On envoit DiffusionNumerotationPokerMessage | " + id + " | " + nom + " | true\n");
                    }
                    break;
                    
                case MESSAGE_NUMEROTATION:
                    ffw.write("****************\n");
                    ffw.write("Traitement de la numérotation\n");
                    
                    DiffusionNumerotationPokerMessage msgNumerotation_temp = (DiffusionNumerotationPokerMessage) pm;
                    
                    ffw.write("On reçoit DiffusionNumerotationPokerMessage | " + msgNumerotation_temp.getNumero() + " | " + msgNumerotation_temp.getNomSender()+ " | " + msgNumerotation_temp.getEcouterSonBroadcast() +"\n");
                    
                    if ((msgNumerotation_temp.getNomSender().compareTo(nom) != 0)) { //|| (msgNumerotation_temp.getEcouterSonBroadcast())
                        ReponseNumerotationPokerMessage reponseNumerotationMsg;
                        if (id == msgNumerotation_temp.getNumero()) {
                            reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.CONFLIT);
                            ffw.write("CONFLIT avec moi et " + msgNumerotation_temp.getNomSender() + " sur le numéro ");
                            ffw.write("On répond avec ReponseNumerotationPokerMessage | " + TypeReponseNumerotation.CONFLIT +"\n");
                            System.err.println("CONFLIT sur le numero moi :" + id + "lui:" + msgNumerotation_temp.getNumero() + "avec le joueur : " + msgNumerotation_temp.getNomSender() +"\n");
                        } else {
                            reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.OK);
                            ffw.write("On répond avec ReponseNumerotationPokerMessage | " + TypeReponseNumerotation.OK +"\n");
                            System.out.println("Numero OK");
                        }
                        reso.sendMessage(from, msgNumerotation_temp.getNomSender(), reponseNumerotationMsg);
                    }
                    
                    break;
                    
                case REPONSE_NUMEROTATION:
                    ffw.write("****************\n");
                    ffw.write("Traitement de la réponse de numérotation de " + from + "\n");
                    
                    ReponseNumerotationPokerMessage msgReponseNumerotation_temp = (ReponseNumerotationPokerMessage) pm;
                    if (msgReponseNumerotation_temp.getReponse() == TypeReponseNumerotation.CONFLIT) {
                        ffw.write("On reçoit ReponseNumerotationPokerMessage | " + TypeReponseNumerotation.CONFLIT +"\n");
                        id = alea(0, listJoueurs.size() - 1);
                        System.out.println("Mon nouvel Id : " + id);
                        DiffusionNumerotationPokerMessage newNumerotationMsg = new DiffusionNumerotationPokerMessage(id, nom, false);
                        ffw.write("On répond DiffusionNumerotationPokerMessage | " + id + " | " + nom + " | false\n");
                        reso.broadcastMessage(from, newNumerotationMsg);
                        nbConflit = nbConflit + (listJoueurs.size() - 2);
                    } else if (msgReponseNumerotation_temp.getReponse() == TypeReponseNumerotation.OK) {
                        ffw.write("On reçoit ReponseNumerotationPokerMessage | " + TypeReponseNumerotation.OK +"\n");
                        numerotationOk++;
                        if (numerotationOk == nbConflit) {
                            System.out.println("Numerotation finie" + (numerotationOk) + "/" + nbConflit);
                            ffw.write("Fin de la numérotation\n");
                            ffw.write("On envoit DiffusionFinNumerotationPokerMessage | " + id +"\n");
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
                    ffw.write("****************\n");
                    ffw.write("On reçoit ReponseConnectionPokerMessage | " + from +"\n");
                    ffw.write(from + "nous a accepté\n");
                    
                    if (!existeJoueur(from)) {
                        listJoueurs.add(new Joueur(from));
                        ReponseConnectionPokerMessage msg_temp2 = (ReponseConnectionPokerMessage) msg;
                        System.out.println(msg_temp2.getNom() + " nous a accepte");
                    }
                    break;
                    
                case DIFFUSION_EJECTION:
                    ffw.write("****************\n");ffw.write("On reçoit EjectionPokerMessage\n");
                    System.out.println("Vous arriver trop tard,\nLa partie a deja commence");
                    try {
                        Thread.sleep(10000);
                        //System.exit(0);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case DIFFUSION_FIN_NUMEROTATION:
                    ffw.write("****************\n");
                    ffw.write("Message de fin de numérotation\n");
                    
                    DiffusionFinNumerotationPokerMessage msgFinNumerotation_temp = (DiffusionFinNumerotationPokerMessage) pm;
                    ffw.write("On reçoit DiffusionFinNumerotationPokerMessage | " + msgFinNumerotation_temp.getId() + "\n");
                    numerotationTerminee++;
                    for (Joueur adv : listJoueurs) {
                        if (adv.getNom().compareTo(from) == 0) {
                            System.out.println(adv.getNom() + "=>" + msgFinNumerotation_temp.getId());
                            adv.setId(msgFinNumerotation_temp.getId()); //attributuion de l'ID
                        }
                    }
                    
                    ffw.write("Création de l'anneau\n");
                    for (Joueur adv : listJoueurs) {
                        if ((id + 1) % listJoueurs.size() == adv.getId()) {
                            adversaireSuivant = adv;//creation de l'anneau
                        }
                    }
                    
                    if (numerotationTerminee == listJoueurs.size()) {
                        ffw.write("Numérotation terminée\n");
                        System.out.println("::Numerotation Terminee !!!:::" + numerotationTerminee);
                        System.out.println("Pret pour l'election, mon adv suivant est" + adversaireSuivant.getNom());
                        ElectionPokerMessage message = new ElectionPokerMessage(id, id);
                        numeroPlusFort = id;
                        System.out.println("DEBUT ELECTION");
                        ffw.write("On envoit ElectionPokerMessage | " + id + " | " + id + " à notre voisin " + adversaireSuivant.getNom() +"\n");
                        reso.sendMessage(nom, adversaireSuivant.getNom(), message);
                        
                    } else {
                        System.out.println("::Numerotation PAS Terminee !!!:::" + numerotationTerminee);
                    }
                    break;
                    
                case MESSAGE_ELECTION:
                    ffw.write("****************\n");
                    ffw.write("Réception d'un message d'élection\n");
                    
                    ElectionPokerMessage msgElection_temp = (ElectionPokerMessage) pm;
                    System.out.println("numLePlusFort : " + numeroPlusFort + "idRecu" + msgElection_temp.getNumeroPlusFort());
                    ffw.write("On reçoit ElectionPokerMessage | " + msgElection_temp.getId() + " | " + msgElection_temp.getNumeroPlusFort() + "\n");
                    
                    if (msgElection_temp.getNumeroPlusFort() > numeroPlusFort) {
                        ffw.write(msgElection_temp.getNumeroPlusFort() + " > " + numeroPlusFort + " => MAJ du numéro le plus fort\n");
                        numeroPlusFort = msgElection_temp.getNumeroPlusFort();
                        ffw.write("On envoit ElectionPokerMessage | " + msgElection_temp.getId() + " | " +  numeroPlusFort +"\n");
                        reso.sendMessage(nom, adversaireSuivant.getNom(), new ElectionPokerMessage(msgElection_temp.getId(), numeroPlusFort));
                        System.out.println("numleplusfort : " + numeroPlusFort);
                    } else if (msgElection_temp.getNumeroPlusFort() < numeroPlusFort) {
                        ffw.write(msgElection_temp.getNumeroPlusFort() + " < " + numeroPlusFort + " => on est plus fort\n");
                        ffw.write("On envoit ElectionPokerMessage | " + msgElection_temp.getId() + " | " +  numeroPlusFort +"\n");
                        reso.sendMessage(nom, adversaireSuivant.getNom(), new ElectionPokerMessage(msgElection_temp.getId(), numeroPlusFort));
                    } else if (msgElection_temp.getNumeroPlusFort() == id && msgElection_temp.getId() == id) {
                        ffw.write(msgElection_temp.getNumeroPlusFort() + " = " + numeroPlusFort + " => on est élu\n");
                        System.out.println("broadcast maitre");
                        jeu = new JeuCartes();
                        ffw.write("On envoit DiffusionMaitre | " + nom +"\n");
                        reso.broadcastMessage(nom, new DiffusionMaitre(nom));
                        ffw.write("On distribue les cartes\n");
                        ffw.write("On envoie en boucle  DistributionPokerMessage | une carte à chaque joueur\n");
                        for (int i = 0; i < 5; i++) {
                            for (Joueur adve : listJoueurs) {
                                DistributionPokerMessage distributionMessage = new DistributionPokerMessage(jeu.nvlleCarte());
                                reso.sendMessage(nom, adve.getNom(), distributionMessage);
                            }
                        }
                    }
                    /*
                    ffw.write("Distribution terminée, on commence les échanges de cartes\n");
                    System.out.println("Envoi du message d'init echange");
                    ffw.write("On envoit InitEchangePokerMessage à notre voisin " + adversaireSuivant.getNom() + "\n");
                    reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                    System.out.println("FIN Envoi du message d'init echange");
                    */
                    break;
                case DIFFUSION_MAITRE:
                    ffw.write("****************\n");
                    ffw.write("Réception du résultat de l'élection\n");
                    DiffusionMaitre msgMaitre_temp = (DiffusionMaitre) pm;                    
                    ffw.write("On reçoit DiffusionMaitre | " + msgMaitre_temp.getMaitre() + "\n");
                    maitre = msgMaitre_temp.getMaitre();
                    System.out.println("maitre : " + msgMaitre_temp.getMaitre().getNom());
                    break;
                    
                case MESSAGE_DISTRIBUTION:
                    ffw.write("****************\n");
                    ffw.write("Réception des cartes\n");
                    
                    DistributionPokerMessage msgDistribution_temp = (DistributionPokerMessage) pm;
                    ffw.write("On reçoit DistributionPokerMessage | " + msgDistribution_temp.getCarte() + "\n");
                    cartes.add(msgDistribution_temp.getCarte());
                    System.out.println(msgDistribution_temp.getCarte());
                    if (cartes.size() == 5) {
                        ffw.write("On a reçut toutes nos cartes\n");
                        System.out.println("Cartes recus, on attend notr tour pour echanger");
                        
                        if (from.compareTo(nom) == 0) {
                            ffw.write("On lance l'étape de changement des cartes\n");
                            System.out.println("Envoi du message d'init echange");
                            ffw.write("On envoit InitEchangePokerMessage à notre voisin " + adversaireSuivant.getNom() +"\n");
                            reso.sendMessage(nom, adversaireSuivant.getNom(), new InitEchangePokerMessage());
                            System.out.println("FIN Envoi du message d'init echange");
                            
                        }
                    }
                    
                    break;
                    
                case REPONSE_AU_MAITRE:
                    ffw.write("****************\n");
                    ffw.write(from + "veut échanger des cartes\n");
                    
                    ReponseMaitrePokerMessage msgReponseMaitre_temp = (ReponseMaitrePokerMessage) pm;
                    ffw.write("On reçoit ReponseMaitrePokerMessage | " + msgReponseMaitre_temp.getCarte() +"\n");
                    System.out.println("Carte recu de l echange : " + msgReponseMaitre_temp.getCarte());
                    jeu.ajoutCarte(msgReponseMaitre_temp.getCarte());
                    Carte nouvelleCarte = jeu.nvlleCarte();
                    ReponseEchangePokerMessage msgKriss = new ReponseEchangePokerMessage(nouvelleCarte);
                    reso.sendMessage(nom, from, msgKriss);
                    ffw.write("On répond ReponseEchangePokerMessage | " + msgKriss.getCarte() +"\n");
                    System.out.println("Carte envoye " + nouvelleCarte);
                    break;
                    
                case MESSAGE_ECHANGE:
                    ffw.write("****************\n");
                                        
                    EchangePokerMessage msgEchange_temp = (EchangePokerMessage) pm;
                    System.out.println("ordinal message" + msgEchange_temp.getTypeEchange() + " ordinal enum" + TypeEchange.ECHANGE);
                    if (msgEchange_temp.getTypeEchange().ordinal() == TypeEchange.ECHANGE.ordinal()) {
                        System.out.println("ECHANGE");
                        ReponseEchangePokerMessage msg_EchangeCarte = (ReponseEchangePokerMessage) msgEchange_temp;
                        cartes.add(msg_EchangeCarte.getCarte());
                        System.out.println("Nouvelle carte : " + msg_EchangeCarte.getCarte());
                        ffw.write("On  a reçut un " + msg_EchangeCarte.getCarte() + "\n");
                    } else {
                        ffw.write("Initialisation de l'échange\n");
                        System.out.println("INIT");
                        nbCarteAChanger = alea(2, 5);
                        System.out.println("Carte a echanger : " + nbCarteAChanger);
                    }
                    if (nbCarteAChanger > 0) {
                        ffw.write("POn veut échanger "+ nbCarteAChanger + " cartes\n");
                        ReponseMaitrePokerMessage reponseEchange = new ReponseMaitrePokerMessage(cartes.remove(0));
                        System.out.println("Nom du maitre : " + maitre.getNom());
                        reso.sendMessage(nom, maitre.getNom(), reponseEchange);
                        nbCarteAChanger--;
                        System.out.println("Carte a echangee : " + nbCarteAChanger + " j'envoie : " + reponseEchange.getCarte());
                    } else {
                        ffw.write("Fin de l'échange, on passe le jeton à " + adversaireSuivant.getNom() + "\n");
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
                    ffw.write("****************\n");
                    ffw.write("On affiche une carte\n");
                    ffw.write("On reçoit AfficheCartePokerMessage\n");
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
            ffw.flush();
            //}
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
