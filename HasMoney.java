import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 18.11.13
 * Time: 0:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class HasMoney {
    protected AtomicInteger money;
    protected int id;
    protected String name;


    public HasMoney(int id) {
        this.id = id;
        this.name = getClass().getName() + id;
        this.money = new AtomicInteger(0);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void add(int v) {
        money.addAndGet(v);
    }

    public void withdraw(int v) {
        money.addAndGet(-v);
    }

    public int getMoney() {
        return money.intValue();
    }

    public String toString() {
        return getClass().toString() + id;
    }


}
