/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
public class Cashier extends Thread {
    private Bank bank;
    private Logger logger;

    public Cashier(Bank bank, Logger logger) {
        this.bank = bank;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (bank.getQueue()) {
                    bank.getQueue().wait();
                }
                Operation operation = bank.getQueue().remove();
                Thread.sleep(1000);

                Transaction tx = new Transaction(operation);
                bank.lockTransactionStart();
                synchronized (tx.outerLock) {
                    synchronized (tx.innerLock) {
                        bank.unlockTransactionStart();

                        //simulating the client asking for balance
                        int money = bank.getBalance(operation.getMoneySource());
                        if (!operation.initiator.wantToContinue(operation, money)) {
                            logger.logRefuse(operation, money);
                            continue;
                        }
                        boolean success = bank.performOperation(operation);
                        logger.logOperation(operation, success);
                    }
                }

            } catch (InterruptedException e) {
                break;
            }
        }
    }


}
