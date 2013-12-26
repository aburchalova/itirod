/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws FileNotFoundException{
        Logger logger = new Logger(new PrintStream(new FileOutputStream("bank_output15")));
        Bank bank = new Bank();
        BankRandomizer randomizer = new BankRandomizer(bank);

        addClients(bank, 100000);
        createCashiers(bank, logger, 50);
        new Supervisor(bank, logger).start();

        while (true) {
            bank.enqueueOperation(randomizer.randomOperation());

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void addClients(Bank bank, int count) {
        int defaultCashAmount = 10000;
        for (int i = 1; i <= count; i++) {
            Client client = new Client(i);
            bank.createAccountForClient(client, defaultCashAmount);
        }
    }

    public static void createCashiers(Bank bank, Logger logger, int count) {
        for (int i = 1; i <= count; i++)
            new Cashier(bank, logger).start();
    }
}