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
                boolean success = bank.performOperation(operation);
                logger.logOperation(operation, success);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
