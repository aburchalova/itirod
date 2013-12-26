/**
 * Created by IntelliJ IDEA.
 * User: angie
 * Date: 17.11.13
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class Client extends HasMoney{

    public Client(int id) {
        super(id);
    }

    public boolean wantToContinue(Operation operation, int sourceMoney) {
        return operation.amount < sourceMoney;
    }

}
