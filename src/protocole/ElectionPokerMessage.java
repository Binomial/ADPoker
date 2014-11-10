package protocole;


public class ElectionPokerMessage extends PokerMessage{
    private int id;

    public ElectionPokerMessage(int id) {
        super(TypeMessage.MESSAGE_ELECTION);
        this.id = id;
    }

    public int getId() {
        return id;
    }    
    
}
