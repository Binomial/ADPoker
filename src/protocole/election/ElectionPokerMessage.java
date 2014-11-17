package protocole.election;

import adPoker.Client;
import adPoker.Joueur;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.JeuCartes;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.distribution.DistributionPokerMessage;

public class ElectionPokerMessage extends PokerMessage {

    private int id;
    private int numeroPlusFort;

    public ElectionPokerMessage(int id, int numeroPlusFort) {
        super(TypeMessage.MESSAGE_ELECTION);
        this.id = id;
        this.numeroPlusFort = numeroPlusFort;
    }

    public int getNumeroPlusFort() {
        return numeroPlusFort;
    }

    public int getId() {
        return id;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            if (getNumeroPlusFort() > cli.getNumeroPlusFort()) {
                cli.getLogger().write("ElectionPokerMessage", from, "mon ID : " + cli.getId() + " < numero Plus Fort : " + getNumeroPlusFort());
                cli.setNumeroPlusFort(numeroPlusFort);
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new ElectionPokerMessage(getId(), cli.getNumeroPlusFort()));                
            } else if (getNumeroPlusFort() < cli.getNumeroPlusFort()) {
                cli.getLogger().write("ElectionPokerMessage", from, "mon ID : " + cli.getId() + " > numero Plus Fort : " + getNumeroPlusFort());
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new ElectionPokerMessage(getId(), numeroPlusFort));
            } else if (getNumeroPlusFort() == cli.getId() && getId() == cli.getId()) {
                cli.getLogger().write("ElectionPokerMessage", from, "mon ID : " + cli.getId() + " == numero Plus Fort : " + getNumeroPlusFort() + "=> MAITRE");
                cli.setJeu(new JeuCartes());
                cli.getReso().broadcastMessage(cli.getNom(), new DiffusionMaitre(cli.getNom()));                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
