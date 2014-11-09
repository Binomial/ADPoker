package adPoker;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import jeuCarte.Carte;
import protocole.DiffusionConnectionPokerMessage;
import protocole.DiffusionDebutNumerotationPokerMessage;
import reso.IReso;

public class Joueur implements Serializable {
    private static final long serialVersionUID = -3879530234484702429L;

    private final String nom;
    private int id;
    private List<Carte> mainCarte;
    private List<Adversaire> adversaires;
    private IReso reso;
    private Client client;
    private int nbFinNumerotation;

    public Joueur(String nom) throws RemoteException, NotBoundException, MalformedURLException {
        this.nom = nom;
        client = new Client(nom);
        nbFinNumerotation = 0;
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

    public void setId(int id) {
        this.id = id;
    }

    public List<Carte> getMainCarte() {
        return mainCarte;
    }

    public void setMainCarte(List<Carte> mainCarte) {
        this.mainCarte = mainCarte;
    }

    public List<Adversaire> getAdversaires() {
        return adversaires;
    }

    public IReso getReso() {
        return reso;
    }

    public void setReso(IReso reso) {
        this.reso = reso;
    }

    public void connection(String ip, int port) throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        setReso((IReso) Naming.lookup(IReso.NAME));
        reso.declareClient(this.nom, client);
        DiffusionConnectionPokerMessage msg2 = new DiffusionConnectionPokerMessage(nom);
        reso.broadcastMessage(nom, msg2);
    }

    public void ecoute() throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        Thread.sleep(20000);
        if (client.getEnEcoute()) {
            System.out.println("FIN DU CHRONO | On fait un broadcast de fin d'attente");
            client.setEnEcoute(false);
            adversaires = client.getAdversaires();
            reso.broadcastMessage(nom, new DiffusionDebutNumerotationPokerMessage(adversaires));
            System.out.println("mon ID : " + client.getId());
            System.out.println("mon Nom : " + client.getNom());
        }
    }

    public static void main(String[] args) {

        try {
            String nom = args[0];
            Joueur joueurLocal = new Joueur(nom);            
            joueurLocal.connection("localhost", IReso.PORT);
            joueurLocal.ecoute();

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        } catch (NotBoundException | InterruptedException ex) {
            //Logger.getLogger(Joueur.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ERROR2");
        }
    }

}
