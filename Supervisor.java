import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
public class Supervisor extends Thread implements Observer {
    private Bank bank;
    private Logger logger;

    private List<Client> clientList;
    private List<Account> accountList;
    private List<HasMoney> processed;
    private final Boolean processed_sync = false; //syncing should be on a final field
    private AtomicInteger correction;

    private int clientsSize;
    private int accountsSize;

    public Supervisor(Bank bank, Logger logger) {
        this.bank = bank;
        this.logger = logger;
        this.bank.addObserver(this);
    }

    private int getClientsSize() {
        return clientList.size();
    }

    private int getAccountsSize() {
        return accountList.size();
    }

    //for the case when clients/accounts can be deleted/added/reordered - add copying of data?
    private void copyData(Bank bank) {
        synchronized (processed_sync) {
            clientList = bank.getClients();
            accountList = bank.getAccounts();
            clientsSize = clientList.size();
            accountsSize = accountList.size();
            processed = new ArrayList<HasMoney>(clientsSize + accountsSize);
            correction = new AtomicInteger(0);
        }

    }

    //also has to work in case of adding new initiator and blank moneySource while summing
    private int getTotalAmount() {
        int total = 0;
        int clientIdx = 0;
        int accountIdx = 0;
        copyData(bank);
        // if all clients will go before all accounts (or vise versa),
        // all operations will need correction (as they will be performed between
        // already supervised and not supervised yet)
        // Adding one moneySource and one initiator decreases such probability.
        int minSize = Math.min(accountsSize, clientsSize);
        for (int counter = 0; counter < minSize; counter++) {
            total = addAccountMoney(total, accountIdx++);
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
            total = addClientMoney(total, clientIdx++);
        }
        return addRest(total, clientIdx, accountIdx) + correction.intValue();
    }

    private int addAccountMoney(int total, int accountIdx) {
        Account currentAccount = accountList.get(accountIdx);

            synchronized (currentAccount) {
                synchronized (processed_sync) {
                total += currentAccount.getMoney();
                processed.add(currentAccount);
            }
        }

        return total;
    }

    private int addClientMoney(int total, int clientIdx) {
        Client currentClient = clientList.get(clientIdx);

            synchronized (currentClient) {
                synchronized (processed_sync) {
                total += currentClient.getMoney();
                processed.add(currentClient);
            }
        }

        return total;
    }

    private int addRest(int total, int clientIdx, int accountIdx) {
        if (accountsSize < clientsSize) {
            total = addClientsRest(total, clientIdx);
        }
        else {
            total = addAccountsRest(total, accountIdx);
        }
        return total;
    }

    private int addAccountsRest(int total, int startAccountIdx) {
        for (int counter = startAccountIdx; counter < accountsSize; counter++) {
            total = addAccountMoney(total, counter);
        }
        return total;
    }

    private int addClientsRest(int total, int startClientIdx) {
        for (int counter = startClientIdx; counter < clientsSize; counter++) {
            total = addClientMoney(total, counter);
        }
        return total;
    }



    @Override
    public void run() {
        int amount = getTotalAmount();
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }

            int newAmount = getTotalAmount();
            logger.logStatus(newAmount == amount, newAmount, correction.intValue());
        }
    }

    // if operation arguments aren't both processed / not processed
    // the total amount needs later correction
    //
    private int correctionValue(Operation operation) {
        ArrayList<Integer> pc = processedClients();
        ArrayList<Integer> pa = processedAccounts();

        boolean containsDest = processed.contains(operation.getMoneyDestination());
        boolean containsSource = processed.contains(operation.getMoneySource());

        if (containsDest && !containsSource) {
            logger.logCorrection(operation, pc, pa);
            return (operation.getAmount());
        }
        if (containsSource && !containsDest) {
            logger.logCorrection(operation, pc, pa);
            return (-operation.getAmount());
        }
        return 0;
    }

    public synchronized void update(Observable o, Object arg) {
        Operation operation = (Operation)arg;
        synchronized (processed_sync) {
            int correctionValue = correctionValue(operation);
            correction.addAndGet(correctionValue);
        }
    }

    ArrayList<Integer> processedClients() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Iterator<HasMoney> it = processed.iterator(); it.hasNext();) {
            HasMoney next = it.next();
            if ( next instanceof Client )
                result.add(next.id);
        }
        return result;
    }

    ArrayList<Integer> processedAccounts() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Iterator<HasMoney> it = processed.iterator(); it.hasNext();) {
            HasMoney next = it.next();
            if ( next instanceof Account )
                result.add(next.id);
        }
        return result;
    }
}
