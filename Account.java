import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class Account extends HasMoney {
    public Account(int id, int initialCash) {
        super(id);
        money = new AtomicInteger(initialCash);
    }

    public Account(int id) {
        this(id, 0);
    }

}
