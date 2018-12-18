package exchangetask;

import java.sql.Connection;

public class Runner {

    public static void main(String[] args) {
        MySQLconnector mySQLconnector = new MySQLconnector();
        Connection connection = new MySQLconnector().getConnection();

        mySQLconnector.resetDB(connection); //This method should be called only once

        ImplementationToDB perform = new ImplementationToDB(connection);

        Order orderBuy1 = perform.createOrder(true, 100, 50);
        Order orderBuy2 = perform.createOrder(true, 120, 60);
        Order orderBuy3 = perform.createOrder(true, 150, 70);
        Order orderSell1 = perform.createOrder(false, 110, 150);
        Order orderSell2 = perform.createOrder(false, 120, 160);
        Order orderSell3 = perform.createOrder(false, 150, 170);

        System.out.println(perform.getTotalSizeAtPrice(100));
        System.out.println(perform.getTotalSizeAtPrice(100, false));
        System.out.println(perform.getHighestBuyPrice());
        System.out.println(perform.getLowestSellPrice());
        perform.send(orderBuy1.getId());
        perform.send(orderSell1.getId());
        perform.modify(orderBuy3.getId(), 200, 100);
        perform.cancel(orderSell3.getId());

    }
}
