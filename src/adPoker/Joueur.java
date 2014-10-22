package adPoker;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.Carte;
import protocole.DiffusionConnectionPokerMessage;
import protocole.DiffusionElectionPokerMessage;
import reso.IReso;

public class Joueur implements Serializable {

    private static final long serialVersionUID = -3879530234484702429L;

    private final String nom;
    private int id;
    private ArrayList<Carte> mainCarte;
    private boolean jeton;
    private boolean maitre;
    private ArrayList<String> adversaires;
    private IReso reso;
    private String nomVoisin;

    private Client client;
    
    public Joueur(String nom) throws NotBoundException, MalformedURLException {
        
            this.nom = nom;
            this.jeton = false;
            this.maitre = false;
        try{
            client = new Client(nom);
        } catch (RemoteException ex) {
            Logger.getLogger(Joueur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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

    public ArrayList<Carte> getMainCarte() {
        return mainCarte;
    }

    public void setMainCarte(ArrayList<Carte> mainCarte) {
        this.mainCarte = mainCarte;
    }

    public ArrayList<String> getAdversaires() {
        return adversaires;
    }

    public void setAdversaires(ArrayList<String> adversaires) {
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
    public void connection(String ip, int port)
            throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        setReso((IReso) Naming.lookup(IReso.NAME));
        reso.declareClient(this.nom, client);
        System.out.println("Declaration envoyee");
        DiffusionConnectionPokerMessage msg2 = new DiffusionConnectionPokerMessage(nom);
        reso.broadcastMessage(nom, msg2);
        System.out.println("Diffusion envoyee");          
    }

    // Attente d'une minute avant d'envoyer un message de fin d'?coute (fin des connections)
    public void finEcoute() throws NotBoundException, MalformedURLException, RemoteException, InterruptedException {
        Thread.sleep(10000);
        if(client.getEnEcoute()) {
            System.out.println("FIN DU CHRONO | On fait un broadcast de fin d'attente");
            client.setEnEcoute(false);
            reso.broadcastMessage(nom, new DiffusionElectionPokerMessage(adversaires));
            Thread.sleep(3000);
        }
    }

    // Le joueur qui a le jeton prend la valeur du jeton, 
    // incr?mente cette valeur, 
    // met ? jour le jeton, et l'envoi ? son voisin
    public int numeroAlea() {
       int num = 0;
       return num;
    }
    
    /*public void registry() {
        try {
            Registry registry = LocateRegistry.createRegistry(IClient.PORT);

            IClient cl = new Client();
            registry.rebind(IClient.NAME, cl);

            System.out.println("Client successfully launched!");
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }
    */
    public static void main(String[] args) {
        try {
            //System.out.println(args.length);
            String nom = args[0];
            
             Joueur joueurLocal = new Joueur(args[0]);
             
            // Le premier joueur enregistre ICLient
         /*   if(args.length == 2) {
                System.out.println("Registry ok");
                joueurLocal.registry();
            }
*/
            joueurLocal.connection("localhost", IReso.PORT);                    
            joueurLocal.finEcoute();
           
        } catch (NotBoundException | MalformedURLException | RemoteException | InterruptedException ex) {
            Logger.getLogger(Joueur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
