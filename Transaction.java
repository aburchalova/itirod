import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 27.12.13
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class Transaction {
    protected Operation operation;

    protected HasMoney outerLock;
    protected HasMoney innerLock;

    public Transaction(Operation operation) {
        this.operation = operation;
        boolean sourceFirst = Transaction.outerLockOnSource(operation);
        outerLock = getOuterLock(operation, sourceFirst);
        innerLock = getInnerLock(operation, sourceFirst);
    }

    //if source is client or source has lower id
    protected static boolean outerLockOnSource(Operation operation) {
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

    // if two clients are transferring money from same two accounts
    // but 1st client is withdrawing from 1st acc
    // and 2nd client is withdrawing from 2nd acc
    // a deadlock will be
    //
    // So let's take as outer lock the one with lowest id.
    // If operation is performed between client and account,
    // client will be outer
    protected static HasMoney getOuterLock(Operation operation, boolean sourceFirst) {
        return sourceFirst ? operation.getMoneySource() : operation.getMoneyDestination();
    }

    protected static HasMoney getInnerLock(Operation operation, boolean sourceFirst) {
        return sourceFirst ? operation.getMoneyDestination() : operation.getMoneySource();
    }
}
