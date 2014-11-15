package protocole.distribution;

import protocole.PokerMessage;
import protocole.TypeMessage;


public class FinDistributionOkPokerMessage extends PokerMessage{

    public FinDistributionOkPokerMessage() {
        super(TypeMessage.MESSAGE_FIN_OK_DISTRIBUTION);
    }

}
