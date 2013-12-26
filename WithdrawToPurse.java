/**
 * Class for withdrawing funds from moneySource and
 * adding them to a initiator.
 */
public class WithdrawToPurse extends Operation {
    public WithdrawToPurse(Client client, Account account, int amount) {
        super(client, account, client, amount);
    }
}
