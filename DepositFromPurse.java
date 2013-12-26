/**
 * Class for adding funds to an account and
 * withdrawing them from a initiator.
 */
public class DepositFromPurse extends Operation {
    public DepositFromPurse(Client client, Account account, int amount) {
        super(client, client, account, amount);
    }
}
