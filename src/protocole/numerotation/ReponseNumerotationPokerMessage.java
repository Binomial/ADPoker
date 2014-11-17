package protocole.numerotation;

import adPoker.Client;
import static adPoker.Client.alea;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class ReponseNumerotationPokerMessage extends PokerMessage {

    private TypeReponseNumerotation reponse;

    public ReponseNumerotationPokerMessage(TypeReponseNumerotation typeReponse) {
        super(TypeMessage.REPONSE_NUMEROTATION);
        reponse = typeReponse;
    }

    public TypeReponseNumerotation getReponse() {
        return reponse;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            if (getReponse() == TypeReponseNumerotation.CONFLIT) {
                cli.setId(Client.alea(0, cli.getListJoueurs().size() - 1));
                System.out.println("Mon nouvel Id : " + cli.getId());
                DiffusionNumerotationPokerMessage newNumerotationMsg = new DiffusionNumerotationPokerMessage(cli.getId(), cli.getNom(), false);
                cli.getReso().broadcastMessage(from, newNumerotationMsg);
                cli.setNbConflit(cli.getNbConflit() + (cli.getListJoueurs().size() - 2));
            } else if (getReponse() == TypeReponseNumerotation.OK) {
                cli.setNumerotationOk(cli.getNumerotationOk() + 1);
                if (cli.getNumerotationOk() == cli.getNbConflit()) {
                    System.out.println("Numerotation finie" + cli.getNumerotationOk() + "/" + cli.getNbConflit());
                    cli.getReso().broadcastMessage(cli.getNom(), new DiffusionFinNumerotationPokerMessage(cli.getId()));
                } else {
                    System.out.println("Numerotation pas finie" + cli.getNumerotationOk() + "/" + cli.getNbConflit());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
