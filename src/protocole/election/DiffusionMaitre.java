package protocole.election;

import adPoker.Client;
import adPoker.Joueur;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class DiffusionMaitre extends PokerMessage {

    private Joueur maitre;

    public DiffusionMaitre(String nom) {
        super(TypeMessage.DIFFUSION_MAITRE);
        maitre = new Joueur(nom);
    }

    public Joueur getMaitre() {
        return maitre;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setMaitre(maitre);
            cli.getLogger().write("DiffusionMaitre", from, "Election maitre : " + getMaitre().getNom());
            cli.getReso().sendMessage(cli.getNom(), cli.getMaitre().getNom(), new ReponseFinElectionPokerMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(DiffusionMaitre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
