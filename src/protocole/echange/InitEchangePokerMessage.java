package protocole.echange;

import adPoker.Client;

public class InitEchangePokerMessage extends EchangePokerMessage {

    public InitEchangePokerMessage() {
        super(TypeEchange.INIT);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        System.out.println("INIT");
        cli.setNbCarteAChanger(Client.alea(2, 5));
        System.out.println("Carte a echangee : " + cli.getNbCarteAChanger());
        traitement(cli, from);
    }

}
