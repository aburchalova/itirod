/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Bank extends Observable{
    private List<Client> clients = new ArrayList<Client>(42);
    private List<Account> accounts = new ArrayList<Account>(42);

    private Queue<Operation> queue = new ConcurrentLinkedQueue<Operation>();
    private ArrayList<Observer> observers = new ArrayList<Observer>();

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

        // if two clients are transferring money from same two accounts
        // but 1st client is withdrawing from 1st acc
        // and 2nd client is withdrawing from 2nd acc
        // a deadlock will be
        //
        // So let's take as outer lock the one with lowest id.
        // If operation is performed between client and account,
        // client will be outer
        HasMoney outerLock;
        HasMoney innerLock;
        if (outerLockOnSource(operation)) {
            outerLock = operation.moneySource;
            innerLock = operation.moneyDestination;
        }
        else {
            innerLock = operation.moneySource;
            outerLock = operation.moneyDestination;
        }
        synchronized (outerLock) {
            synchronized (innerLock){
                result =  operation.perform();
                setChanged();
                if (result) {
                    for (Observer o: observers) {
                        o.update(this, operation);
                    }
                }

            }
        }
        return result;
    }

    //if source is client or source has lower id
    protected boolean outerLockOnSource(Operation operation) {
        Class sourceCl = operation.moneySource.getClass();
        Class destCl = operation.moneyDestination.getClass();
        if (sourceCl == Client.class) {
            return true;
        }
        if (sourceCl == destCl) {
            //return with lowest id
            return operation.moneySource.getId() < operation.moneyDestination.getId();
        }
        return false;
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
}
