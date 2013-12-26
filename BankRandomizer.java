import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 01.12.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class BankRandomizer {
    private Bank bank;
    private int operationAmountLimit;
    private static int defaultOperationAmountLimit = 100;
    private Random random = new Random(1);

    public BankRandomizer(Bank bank) {
        this(bank, defaultOperationAmountLimit);
    }

    public BankRandomizer(Bank bank, int operationAmountLimit) {
        this.bank = bank;
        this.operationAmountLimit = operationAmountLimit;
    }

    public Operation randomOperation() {
        Client initiator = randomClient();
        HasMoney source = randomAccount();
        HasMoney dest = randomAccount();

        return new Operation(initiator, source, dest, randomOperationAmount());
    }

    public Client randomClient() {
        return bank.getClients().get(randomClientNum());
    }

    public Account randomAccount() {
        return bank.getAccounts().get(randomAccountNum());
    }

    public int randomOperationAmount() {
        return random.nextInt(operationAmountLimit) + 1;
    }

    public static int getDefaultOperationAmountLimit() {
        return defaultOperationAmountLimit;
    }

    public static void setDefaultOperationAmountLimit(int value) {
        defaultOperationAmountLimit = value;
    }

    private int randomClientNum() {
        return random.nextInt(clientsLength());
    }

    private int randomAccountNum() {
        return random.nextInt(accountsLength());
    }

    private int clientsLength() {
        return bank.getClients().size();
    }

    private int accountsLength() {
        return bank.getAccounts().size();
    }
}
