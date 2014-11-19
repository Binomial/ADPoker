package adPoker;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import jeuCarte.Carte;
import protocole.connection.DiffusionConnectionPokerMessage;
import protocole.numerotation.DiffusionDebutNumerotationPokerMessage;
import reso.IReso;

public class MainClient implements Serializable {
    private static final long serialVersionUID = -3879530234484702429L;

    private final String nom;
    private int id;
    private List<Carte> mainCarte;
    private List<Joueur> adversaires;
    private IReso reso;
    private Client client;
    private int nbFinNumerotation;

    public MainClient(String nom) throws RemoteException, NotBoundException, MalformedURLException {
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

    public List<Joueur> getAdversaires() {
        return adversaires;
    }

    public IReso getReso() {
        return reso;
    }

    public void setReso(IReso reso) {
        this.reso = reso;
    }

    public void connection(String nomReso) throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        setReso((IReso) Naming.lookup(nomReso));
        reso.declareClient(this.nom, client);
        DiffusionConnectionPokerMessage msg2 = new DiffusionConnectionPokerMessage(nom);
        reso.broadcastMessage(nom, msg2);
    }

    public void ecoute() throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        Thread.sleep(20000);
        if (client.isEnEcoute()) {
            client.setEnEcoute(false);
            adversaires = client.getAdversaires();
            reso.broadcastMessage(nom, new DiffusionDebutNumerotationPokerMessage(adversaires));
        }
    }

    public static void main(String[] args) {

        try {
            String nom = args[0];
            String machine = args[1];
            
            machine = "//" + machine + "/" + IReso.NAME;
            
            MainClient joueurLocal = new MainClient(nom);            
            joueurLocal.connection(machine );
            joueurLocal.ecoute();

        }catch(Exception exx){
            exx.printStackTrace();
        }
    }

}
