/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class Operation implements Callable<Boolean>{
    protected static Random random = new Random();

    // initiator
    protected Client initiator;

    // from which money is taken
    protected HasMoney moneySource;

    // to which money is added
    protected HasMoney moneyDestination;

    protected int amount;

    // failure reasons
    protected ArrayList<String> errors;

    public Operation(Client initiator, HasMoney moneySource, HasMoney moneyDestination, int amount) {
        this.initiator = initiator;
        this.moneySource = moneySource;
        this.moneyDestination = moneyDestination;
        this.amount = amount;
        this.errors = new ArrayList<String>();
    }

//    public static Operation createRandom(Client client, HasMoney moneySource, HasMoney moneyDest, int amount) {
//        if (random.nextBoolean()) {
//            return new DepositFromPurse(client, moneySource, moneyDest, amount);
//        }
//        else {
//            return new WithdrawToPurse(client, account, amount);
//        }
//    }

    public int getAmount() {
        return amount;
    }

    public String getOperationType() {
        return this.getClass().getName().toString();
    }

    public Client getInitiator() {
        return initiator;
    }

    public HasMoney getMoneySource() {
        return moneySource;
    }

    public HasMoney getMoneyDestination() {
        return moneyDestination;
    }

    public boolean isValid() {
        return positiveAmount() && sufficientBalance();
    }

    protected boolean positiveAmount() {
        if (amount > 0) {
            return true;
        }
        else {
            errors.add("Non-positive amount");
            return false;
        }
    }

    protected boolean sufficientBalance() {
        if (amount <= moneySource.getMoney()) {
            return true;
        }
        else {
            errors.add("Insufficient moneySource balance");
            return false;
        }
    }

    /* Performs main operation; without synchronization and validity check */
    public Boolean call() {
        moneySource.withdraw(amount);
        moneyDestination.add(amount);
        return true;
    }

    /* Performs operation if it's valid */
    public Boolean perform() {
        return isValid() && call();
    }

    @Override
    public String toString() {
        String status = StringUtils.join(errors, ", ");
        if (status != "") {
            status = "[" + status + "]";
        }
        String init = initiator.getName();
        String source = moneySource.getName();
        String dest = moneyDestination.getName();
        return String.format("%s initiated %s from %s to %s ($%d) %s", init, getOperationType(), source, dest, amount, status);
    }


}