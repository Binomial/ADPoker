package protocole.echange;
import protocole.PokerMessage;
import protocole.TypeMessage;

public abstract class  EchangePokerMessage extends PokerMessage {

    TypeEchange typeEchange;

    public EchangePokerMessage(TypeEchange type) {
        super(TypeMessage.MESSAGE_ECHANGE);
        this.typeEchange = type;
    }

    public TypeEchange getTypeEchange() {
        return typeEchange;
    }
    
}
