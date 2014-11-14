package protocole.election;

import protocole.PokerMessage;
import protocole.TypeMessage;


public class ElectionPokerMessage extends PokerMessage{
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
    
}
