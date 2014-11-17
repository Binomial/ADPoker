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
import jeuCarte.Carte;
import jeuCarte.JeuCartes;
import protocole.*;

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
    private int nbFinElection;
    //distribution
    private int nbDistribOk;
    //echange
    private int nbCarteAChanger;
    private int nbTour;
    public static final int NB_TOUR_MAX = 1;
    //fin
    private int nbFinOk;

    private ADLogger logger;

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
        nbDistribOk = 0;
        nbTour = 0;
        nbFinOk = 0;
        nbFinElection = 0;

        logger = new ADLogger(nom);
    }

    List<Joueur> getAdversaires() {
        return listJoueurs;
    }

    public void setEnEcoute(boolean enEcoute) {
        this.enEcoute = enEcoute;
    }

    public boolean isEnEcoute() {
        return enEcoute;
    }

    public ADLogger getLogger() {
        return logger;
    }

    public int getNbFinElection() {
        return nbFinElection;
    }

    public void setNbFinElection(int nbFinElection) {
        this.nbFinElection = nbFinElection;
    }

    public IReso getReso() {
        return reso;
    }

    public void setReso(IReso reso) {
        this.reso = reso;
    }

    public boolean isEjection() {
        return ejection;
    }

    public void setEjection(boolean ejection) {
        this.ejection = ejection;
    }

    public List<Joueur> getListJoueurs() {
        return listJoueurs;
    }

    public void setListJoueurs(List<Joueur> listJoueurs) {
        this.listJoueurs = listJoueurs;
    }

    public List<Carte> getCartes() {
        return cartes;
    }

    public void setCartes(List<Carte> cartes) {
        this.cartes = cartes;
    }

    public JeuCartes getJeu() {
        return jeu;
    }

    public void setJeu(JeuCartes jeu) {
        this.jeu = jeu;
    }

    public int getNumerotationOk() {
        return numerotationOk;
    }

    public void setNumerotationOk(int numerotationOk) {
        this.numerotationOk = numerotationOk;
    }

    public int getNumerotationTerminee() {
        return numerotationTerminee;
    }

    public void setNumerotationTerminee(int numerotationTerminee) {
        this.numerotationTerminee = numerotationTerminee;
    }

    public int getNbConflit() {
        return nbConflit;
    }

    public void setNbConflit(int nbConflit) {
        this.nbConflit = nbConflit;
    }

    public Joueur getAdversaireSuivant() {
        return adversaireSuivant;
    }

    public void setAdversaireSuivant(Joueur adversaireSuivant) {
        this.adversaireSuivant = adversaireSuivant;
    }

    public int getNumeroPlusFort() {
        return numeroPlusFort;
    }

    public void setNumeroPlusFort(int numeroPlusFort) {
        this.numeroPlusFort = numeroPlusFort;
    }

    public Joueur getMaitre() {
        return maitre;
    }

    public void setMaitre(Joueur maitre) {
        this.maitre = maitre;
    }

    public int getNbDistribOk() {
        return nbDistribOk;
    }

    public void setNbDistribOk(int nbDistribOk) {
        this.nbDistribOk = nbDistribOk;
    }

    public int getNbCarteAChanger() {
        return nbCarteAChanger;
    }

    public void setNbCarteAChanger(int nbCarteAChanger) {
        this.nbCarteAChanger = nbCarteAChanger;
    }

    public int getNbTour() {
        return nbTour;
    }

    public void setNbTour(int nbTour) {
        this.nbTour = nbTour;
    }

    public int getNbFinOk() {
        return nbFinOk;
    }

    public void setNbFinOk(int nbFinOk) {
        this.nbFinOk = nbFinOk;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    // Retourne un nombre al?atoire de 0 au nombre d'aversaires
    public static int alea(int min, int max) {
        Random rand = new Random();
        // return rand.nextInt(adversaires.size() - 0 + 1) + 0;
        return rand.nextInt(max - min + 1) + min;
    }

    public boolean existeJoueur(String nomJoueur) {
        for (Joueur joueur : listJoueurs) {
            if (joueur.getNom().compareTo(nomJoueur) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void receiveMessage(String from, Serializable msg) throws RemoteException {
        PokerMessage pm = ((PokerMessage) msg);
        pm.traitementMessage(this, from);
    }
}
