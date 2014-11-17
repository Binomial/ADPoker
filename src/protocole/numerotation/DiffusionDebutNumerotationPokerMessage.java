package protocole.numerotation;

import adPoker.Client;
import adPoker.Joueur;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

/**
 *
 * @class DiffusionConnectionPokerMessage Message envoye des que le timer
 * atteint 1 minute, pour lancer l'attribution des numeros
 */
public class DiffusionDebutNumerotationPokerMessage extends PokerMessage implements Serializable {

    private List<Joueur> joueursList;

    public DiffusionDebutNumerotationPokerMessage(List<Joueur> joueursList) {
        super(TypeMessage.DIFFUSION_NUMEROTATION);
        this.joueursList = joueursList;
    }

    public List<Joueur> getJoueursList() {
        return this.joueursList;
    }

    public int getNbAdversaire() {
        return joueursList.size();
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setEnEcoute(false);
            if (cli.getId() == -1) {
                if (cli.getListJoueurs().size() != getNbAdversaire()) {
                    cli.getLogger().write("DiffusionDebutNumerotationPokerMessage", from, "Mise a jour de la liste des adversaires");
                    cli.setListJoueurs(joueursList);
                }
                cli.setNbConflit(cli.getListJoueurs().size() - 1);                
                cli.getLogger().write("DiffusionDebutNumerotationPokerMessage", from, "Commencement numerotation");
                cli.setId(Client.alea(0, cli.getListJoueurs().size() - 1));
                cli.getLogger().write("DiffusionDebutNumerotationPokerMessage", from, "ID : "+cli.getId());
                DiffusionNumerotationPokerMessage numerotationMsg = new DiffusionNumerotationPokerMessage(cli.getId(), cli.getNom(), true);

                cli.getReso().broadcastMessage(cli.getNom(), numerotationMsg);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
