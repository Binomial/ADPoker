package protocole.numerotation;

import adPoker.Client;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class DiffusionNumerotationPokerMessage extends PokerMessage {

    private int numero;
    private boolean ecouterSonPropreBroadcast;
    private String nomSender;

    public DiffusionNumerotationPokerMessage(int numero, String nom, boolean ecoute) {
        super(TypeMessage.MESSAGE_NUMEROTATION);
        this.numero = numero;
        nomSender = nom;
        ecouterSonPropreBroadcast = ecoute;
    }

    public int getNumero() {
        return numero;
    }

    public boolean getEcouterSonBroadcast() {
        return ecouterSonPropreBroadcast;
    }

    public String getNomSender() {
        return nomSender;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            if ((getNomSender().compareTo(cli.getNom()) != 0)) { //|| (msgNumerotation_temp.getEcouterSonBroadcast())
                ReponseNumerotationPokerMessage reponseNumerotationMsg;
                if (cli.getId() == getNumero()) {
                    reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.CONFLIT);
                    cli.getLogger().write("DiffusionNumerotationPokerMessage", from, "Conflit ID : " + cli.getId() + " | " + getNomSender());
                } else {
                    reponseNumerotationMsg = new ReponseNumerotationPokerMessage(TypeReponseNumerotation.OK);                    
                }
                cli.getReso().sendMessage(cli.getNom(), getNomSender(), reponseNumerotationMsg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
