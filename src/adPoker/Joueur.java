package adPoker;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.Carte;
import protocole.DiffusionConnectionPokerMessage;
import protocole.DiffusionFinAttentePokerMessage;
import protocole.DiffusionNumerotationPokerMessage;
import reso.IReso;

public class Joueur implements Serializable {

    private static final long serialVersionUID = -3879530234484702429L;

    private final String nom;
    private int id;
    private List<Carte> mainCarte;
    private List<String> adversaires;
    private boolean jeton;
    private boolean maitre;
    private IReso reso;
    private String nomVoisin;
    private Client client;

    public Joueur(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        this.nom = nom;
        this.jeton = false;
        this.maitre = false;

        client = new Client(nom);
    }

    public String getNom() {
        return nom;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public boolean isJeton() {
        return jeton;
    }

    public boolean isMaitre() {
        return maitre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJeton(boolean jeton) {
        this.jeton = jeton;
    }

    public void setMaitre(boolean maitre) {
        this.maitre = maitre;
    }

    public List<Carte> getMainCarte() {
        return mainCarte;
    }

    public void setMainCarte(List<Carte> mainCarte) {
        this.mainCarte = mainCarte;
    }

    public List<String> getAdversaires() {
        return adversaires;
    }

    public void setAdversaires(List<String> adversaires) {
        this.adversaires = adversaires;
    }

    public IReso getReso() {
        return reso;
    }

    public void setReso(IReso reso) {
        this.reso = reso;
    }

    // Declare le joueur au reso
    // Envoie le nom au autres joueurs par broadcast
    public void connection(String ip, int port) throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        setReso((IReso) Naming.lookup(IReso.NAME));
       
        reso.declareClient(this.nom, client);
        System.out.println("Declaration envoyee");
        DiffusionConnectionPokerMessage msg2 = new DiffusionConnectionPokerMessage(nom);
        reso.broadcastMessage(nom, msg2);
        System.out.println("Diffusion envoyee");
    }

    // Attente d'une minute avant d'envoyer un message de fin d'ecoute (fin des connections)
    public void ecoute() throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        Thread.sleep(10000);

            System.out.println("FIN DU CHRONO");
            adversaires = client.getAdversaires();
            reso.broadcastMessage(nom, new DiffusionFinAttentePokerMessage(adversaires));
            System.out.println("Tableau adverse envoye");
            System.out.println("dud");
    }

    public static void main(String[] args) {
        try {
            //String nom = args[0];

            Joueur joueurLocal = new Joueur(args[0]);

            joueurLocal.connection("localhost", IReso.PORT);
            joueurLocal.ecoute();

        } catch (NotBoundException | MalformedURLException | RemoteException | InterruptedException ex) {
            Logger.getLogger(Joueur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
