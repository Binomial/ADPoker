package protocole.numerotation;

import adPoker.Client;
import adPoker.Joueur;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.election.ElectionPokerMessage;

public class DiffusionFinNumerotationPokerMessage extends PokerMessage {

    private int id;

    public DiffusionFinNumerotationPokerMessage(int id) {
        super(TypeMessage.DIFFUSION_FIN_NUMEROTATION);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void traitementMessage(Client cli, String from) {

        try {
            cli.setNumerotationTerminee(cli.getNumerotationTerminee() + 1);
            for (Joueur adv : cli.getListJoueurs()) {
                if (adv.getNom().compareTo(from) == 0) {
                    System.out.println(adv.getNom() + "=>" + getId());
                    adv.setId(this.getId()); //attributuion de l'ID
                }
            }
            for (Joueur adv : cli.getListJoueurs()) {
                if (((cli.getId() + 1) % cli.getListJoueurs().size()) == adv.getId()) {
                    
                    cli.setAdversaireSuivant(adv);//creation de l anneau
                }
            }
            if (cli.getNumerotationTerminee() == cli.getListJoueurs().size()) {
                System.out.println("::Numerotation Terminee !!!:::" + cli.getNumerotationTerminee());
                System.out.println("Pr?t pour l'election, mon adv suivant est" + cli.getAdversaireSuivant().getNom());
                ElectionPokerMessage message = new ElectionPokerMessage(id, id);
                cli.setNumeroPlusFort(id);
                System.out.println("DEBUT ELECTION");
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), message);
                System.out.println("DEBUT ELECTION SEND");
            } else {
                System.out.println("::Numerotation PAS Terminee !!!:::" + cli.getNumerotationTerminee());
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
