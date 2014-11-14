package protocole.numerotation;

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
}
