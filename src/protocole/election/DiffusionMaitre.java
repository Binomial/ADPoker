package protocole.election;

import adPoker.Adversaire;
import protocole.PokerMessage;
import protocole.TypeMessage;


public class DiffusionMaitre extends PokerMessage{

    private Adversaire maitre;
    
    public DiffusionMaitre(String nom) {
        super(TypeMessage.DIFFUSION_MAITRE);
        maitre = new Adversaire(nom);
    }

    public Adversaire getMaitre() {
        return maitre;
    }
}
