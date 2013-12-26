/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Bank extends Observable{
    private List<Client> clients = new ArrayList<Client>(42);
    private List<Account> accounts = new ArrayList<Account>(42);

    private Queue<Operation> queue = new ConcurrentLinkedQueue<Operation>();
    private ArrayList<Observer> observers = new ArrayList<Observer>();

    private ReentrantLock cashiersLock = new ReentrantLock();

    public void createAccountForClient(Client client, int initialCash) {
        synchronized (getClients()) {
            synchronized (getAccounts()) {
                if (!clients.contains(client)) {
                    clients.add(client);
                    accounts.add(new Account(client.id, initialCash));
                }
            }
        }

    }

    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void enqueueOperation(Operation operation) {
        synchronized (queue) {
            queue.add(operation);
            queue.notify();
        }
    }

    public boolean performOperation(Operation operation) {
        boolean result;
        // need to avoid self-deadlocks, i.e. when source and dest are the same
        if (operation.moneySource == operation.moneyDestination) {
            return true;
        }

        result =  operation.perform();
        setChanged();
        if (result) {
            for (Observer o: observers) {
                o.update(this, operation);
            }

        }
        return result;
    }

    public Integer getBalance(HasMoney hasMoney) {
        return hasMoney.getMoney();
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Queue<Operation> getQueue() {
        return queue;
    }

    public void lockTransactionStart() {
        cashiersLock.lock();
    }

    public void unlockTransactionStart() {
        cashiersLock.unlock();

    }
}
