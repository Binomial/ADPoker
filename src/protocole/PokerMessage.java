package protocole;

import adPoker.Client;
import java.io.Serializable;

public abstract class PokerMessage implements Serializable, IMessage{

    private TypeMessage type;

    public PokerMessage(TypeMessage type) {
        this.type = type;
    }

    public TypeMessage getType() {
        return type;
    }

    public void setType(TypeMessage type) {
        this.type = type;
    }

}
