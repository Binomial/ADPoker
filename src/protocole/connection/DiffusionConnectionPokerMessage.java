package protocole.connection;

import adPoker.Client;
import adPoker.Joueur;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

/**
 *
 * @class DiffusionConnectionPokerMessage Message envoye apres la declaration du
 * client
 */
public class DiffusionConnectionPokerMessage extends PokerMessage {

    private String nom;

    public String getNom() {
        return nom;
    }

    public DiffusionConnectionPokerMessage(String nom) {
        super(TypeMessage.DIFFUSION_CONNECTION);
        this.nom = nom;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            // la minute n'est pas ecoulee
            if (cli.isEnEcoute()) {
                ReponseConnectionPokerMessage msg2 = new ReponseConnectionPokerMessage(nom);
                if (!cli.existeJoueur(from)) {
                    cli.getListJoueurs().add(new Joueur(from));
                }

                cli.getReso().sendMessage(nom, from, msg2);
                cli.getLogger().write("DiffusionConnectionPokerMessage", from, getNom());
            } else {
                cli.getLogger().write("DiffusionConnectionPokerMessage", from, "Arrive trop tard : EJECTION");
                cli.getReso().sendMessage(nom, from, new EjectionPokerMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
