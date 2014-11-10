package protocole;

import adPoker.Adversaire;


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
