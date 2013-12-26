/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */

import java.io.PrintStream;
import java.util.ArrayList;

public class Logger {
    public PrintStream stream;

    public Logger(PrintStream stream) {
        this.stream = stream;
    }

    public void logOperation(Operation operation, boolean success) {
        String successString = success ? "success" : "rejected";

        stream.printf("%s: %s\n", successString, operation.toString());
    }

    public void logStatus(boolean good, int totalAmount, int correction) {
        stream.printf("status: %s, total: %d, correction: %d\n", good ? "OK" : "error!", totalAmount, correction);
    }

    public void log(String message) {
        stream.println(message);
    }

    public void logCorrection(Operation operation, ArrayList<Integer> processedClients, ArrayList<Integer> processedAccounts) {
        stream.printf("applying correction on operation %s\n", operation.toString());
        int fpc = processedClients.get(0);
        int lpc = processedClients.get(processedClients.size() - 1);

        int fpa = processedAccounts.get(0);
        int lpa = processedAccounts.get(processedAccounts.size() - 1);

//        if (processedClients.toString() == "[]" && processedAccounts.toString() == "[]" || lpc == 100000 && lpa == 100000) {
//            log("!!!!!!!");
//        }
        stream.printf("processed clients: %d - %d, processed accounts: %d - %d\n", fpc, lpc, fpa, lpa);
    }
}
