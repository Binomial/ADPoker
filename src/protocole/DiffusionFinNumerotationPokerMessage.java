package protocole;


public class DiffusionFinNumerotationPokerMessage extends PokerMessage {

    private int id;
    
    public DiffusionFinNumerotationPokerMessage(int id) {
        super(TypeMessage.DIFFUSION_FIN_NUMEROTATION);
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
