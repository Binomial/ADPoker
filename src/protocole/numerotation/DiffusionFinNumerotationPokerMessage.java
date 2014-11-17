package protocole.numerotation;

import adPoker.Client;
import adPoker.Joueur;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.election.ElectionPokerMessage;

public class DiffusionFinNumerotationPokerMessage extends PokerMessage {

    private int id;

    public DiffusionFinNumerotationPokerMessage(int id) {
        super(TypeMessage.DIFFUSION_FIN_NUMEROTATION);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void traitementMessage(Client cli, String from) {

        try {
            cli.setNumerotationTerminee(cli.getNumerotationTerminee() + 1);
            for (Joueur adv : cli.getListJoueurs()) {
                if (adv.getNom().compareTo(from) == 0) {
                    cli.getLogger().write("DiffusionFinNumerotationPokerMessage", from, "Attribution definitive :  " + from + " <= " + cli.getId());
                    adv.setId(this.getId()); //attributuion de l'ID
                }
            }
            for (Joueur adv : cli.getListJoueurs()) {
                if (((cli.getId() + 1) % cli.getListJoueurs().size()) == adv.getId()) {
                    cli.setAdversaireSuivant(adv);//creation de l anneau
                    cli.getLogger().write("DiffusionFinNumerotationPokerMessage", from, "Mon adversaire suivant est " + cli.getAdversaireSuivant().getNom());
                }
            }
            if (cli.getNumerotationTerminee() == cli.getListJoueurs().size()) {
                cli.getLogger().write("DiffusionFinNumerotationPokerMessage", from, "Numerotation terminee");
                ElectionPokerMessage message = new ElectionPokerMessage(id, id);
                cli.setNumeroPlusFort(id);
                cli.getLogger().write("DiffusionFinNumerotationPokerMessage", from, "Debut ELECTION");
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), message);
            } else {
                cli.getLogger().write("DiffusionFinNumerotationPokerMessage", from, "Numerotation en cours");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
