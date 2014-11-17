package protocole;

import adPoker.Client;

public interface IMessage {
    public void traitementMessage(Client cli, String from);
}
