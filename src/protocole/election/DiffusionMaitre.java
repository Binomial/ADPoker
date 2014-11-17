package protocole.election;

import adPoker.Client;
import adPoker.Joueur;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class DiffusionMaitre extends PokerMessage {

    private Joueur maitre;

    public DiffusionMaitre(String nom) {
        super(TypeMessage.DIFFUSION_MAITRE);
        maitre = new Joueur(nom);
    }

    public Joueur getMaitre() {
        return maitre;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        cli.setMaitre(maitre);        
        System.out.println("maitre : " + cli.getMaitre().getNom());
    }
}
