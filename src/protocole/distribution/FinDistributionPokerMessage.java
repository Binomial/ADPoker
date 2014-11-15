package protocole.distribution;

import protocole.PokerMessage;
import protocole.TypeMessage;


public class FinDistributionPokerMessage extends PokerMessage{

    public FinDistributionPokerMessage() {
        super(TypeMessage.DIFFUSION_FIN_DISTRIBUTION);
    }

}
