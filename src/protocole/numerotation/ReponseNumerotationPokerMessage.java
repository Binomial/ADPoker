package protocole.numerotation;

import adPoker.Client;
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
                cli.getLogger().write("ReponseNumerotationPokerMessage", from, "Nouvel ID tire : " + cli.getId());
                DiffusionNumerotationPokerMessage newNumerotationMsg = new DiffusionNumerotationPokerMessage(cli.getId(), cli.getNom(), false);
                cli.getReso().broadcastMessage(from, newNumerotationMsg);
                cli.setNbConflit(cli.getNbConflit() + (cli.getListJoueurs().size() - 2));
            } else if (getReponse() == TypeReponseNumerotation.OK) {
                cli.setNumerotationOk(cli.getNumerotationOk() + 1);
                if (cli.getNumerotationOk() == cli.getNbConflit()) {
                    cli.getLogger().write("ReponseNumerotationPokerMessage", from, "Numerotation terminee : " + cli.getNumerotationOk() + "/" + cli.getNbConflit());
                    cli.getReso().broadcastMessage(cli.getNom(), new DiffusionFinNumerotationPokerMessage(cli.getId()));
                } else {
                    cli.getLogger().write("ReponseNumerotationPokerMessage", from, "Numerotation pas finie : " + cli.getNumerotationOk() + "/" + cli.getNbConflit());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
