package protocole;


public class ReponseNumerotationPokerMessage extends PokerMessage {
    
    private TypeReponseNumerotation reponse;
    
    public ReponseNumerotationPokerMessage(TypeReponseNumerotation typeReponse) {
        super(TypeMessage.REPONSE_NUMEROTATION);
        reponse = typeReponse;
    }

    public TypeReponseNumerotation getReponse() {
        return reponse;
    }
}
