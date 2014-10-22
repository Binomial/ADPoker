package protocole;

/**
 * 
 * @class EjectionPokerMessage
 * Message envoye si la partie est en cours
 */
public class EjectionPokerMessage extends PokerMessage{

    public EjectionPokerMessage() {
        super(TypeMessage.DIFFUSION_EJECTION);
    }
    

}
