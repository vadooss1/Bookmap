package exchangetask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplementationToDB implements AdvancedExchangeInterface, QueryInterface {
    private Connection connection;

    public ImplementationToDB(Connection connection) {
        this.connection = connection;
    }

    public Order createOrder(boolean isBuy, int price, int size) {
        Order order = new Order(isBuy, price, size, false);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO order_service.orders " +
                    "(orderId, sideIsBuy, price, size) values (?, ?, ?, ?)");
            preparedStatement.setLong(1, order.getId());
            preparedStatement.setBoolean(2, order.isSideIsBuy());
            preparedStatement.setInt(3, order.getPrice());
            preparedStatement.setInt(4, order.getSize());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }
    public Order getOrder(long orderId){
        Order order = new Order();
        order.setId(0);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_service.orders WHERE orderId = ?");
            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                order.setId(resultSet.getLong("orderId"));
                order.setSideIsBuy(resultSet.getBoolean("sideIsBuy"));
                order.setPrice(resultSet.getInt("price"));
                order.setSize(resultSet.getInt("size"));
                order.setWorking(resultSet.getBoolean("isWorking"));
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    public void updateOrder(Order order){
        if(order.getSize()<=0){order.setWorking(false);}
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE order_service.orders " +
                    "SET sideIsBuy = ?, price = ?, size = ?, isWorking = ? WHERE orderId = ?");
            preparedStatement.setBoolean(1, order.isSideIsBuy());
            preparedStatement.setInt(2, order.getPrice());
            preparedStatement.setInt(3, order.getSize());
            preparedStatement.setBoolean(4, order.isWorking());
            preparedStatement.setLong(5, order.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean modify(long orderId, int newPrice, int newSize){
        Order order = getOrder(orderId);
        if(order.getId()==0||order.getSize()<=0){
            try {
                throw new RequestRejectedException();
            }catch (RequestRejectedException e){
                System.out.println(e);
                e.getMessage();
                return false;
            }
        }else{
            order.setPrice(newPrice);
            order.setSize(newSize);
            updateOrder(order);
            return true;
        }
    }

    @Override
    public void send(long orderId) {
        Order order = getOrder(orderId);
        if(order.getId()==0||order.getSize()<=0){
            try {
                throw new RequestRejectedException();
            }catch (RequestRejectedException e){
                System.out.println(e);
                e.getMessage();
            }
        }else{
            order.setWorking(true);
            if(order.isSideIsBuy()){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_service.orders" +
                        " WHERE sideIsBuy != ? && isWorking = ? && price <= ? && size > ? ORDER BY price, size DESC");
                preparedStatement.setBoolean(1, order.isSideIsBuy());
                preparedStatement.setBoolean(2, order.isWorking());
                preparedStatement.setInt(3, order.getPrice());
                preparedStatement.setInt(4, 0);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()&&order.getSize()>0){
                    Order order2 = new Order();
                    order2.setId(resultSet.getLong("orderId"));
                    order2.setSideIsBuy(resultSet.getBoolean("sideIsBuy"));
                    order2.setPrice(resultSet.getInt("price"));
                    order2.setSize(resultSet.getInt("size"));
                    order2.setWorking(resultSet.getBoolean("isWorking"));
                    if(order2.getSize()<=order.getSize()){
                        order.setSize(order.getSize()-order2.getSize());
                        order2.setSize(0);
                    }else {
                        order2.setSize(order2.getSize()-order.getSize());
                        order.setSize(0);
                    }
                    updateOrder(order2);
                }
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            updateOrder(order);}else{
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_service.orders" +
                            " WHERE sideIsBuy != ? && isWorking = ? && price >= ? && size > ? ORDER BY price DESC, size DESC");
                    preparedStatement.setBoolean(1, order.isSideIsBuy());
                    preparedStatement.setBoolean(2, order.isWorking());
                    preparedStatement.setInt(3, order.getPrice());
                    preparedStatement.setInt(4, 0);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()&&order.getSize()>0){
                        Order order2 = new Order();
                        order2.setId(resultSet.getLong("orderId"));
                        order2.setSideIsBuy(resultSet.getBoolean("sideIsBuy"));
                        order2.setPrice(resultSet.getInt("price"));
                        order2.setSize(resultSet.getInt("size"));
                        order2.setWorking(resultSet.getBoolean("isWorking"));
                        if(order2.getSize()<=order.getSize()){
                            order.setSize(order.getSize()-order2.getSize());
                            order2.setSize(0);
                        }else {
                            order2.setSize(order2.getSize()-order.getSize());
                            order.setSize(0);
                        }
                        updateOrder(order2);
                    }
                    preparedStatement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                updateOrder(order);
            }
        }

    }

    @Override
    public boolean cancel(long orderId){
        Order order = getOrder(orderId);
        if(order.getId()==0||order.getSize()<=0){
            try {
                throw new RequestRejectedException();
            }catch (RequestRejectedException e){
                System.out.println(e);
                e.getMessage();
                return false;
            }
        }else{
            order.setWorking(false);
            updateOrder(order);
            return true;
        }
    }

    @Override
    public int getTotalSizeAtPrice(int price) {
        int sum = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(size) AS \"totalsize\"" +
                    " FROM order_service.orders WHERE isWorking = ? && price = ?");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, price);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                sum = resultSet.getInt("totalsize");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public int getTotalSizeAtPrice(int price, boolean isBuy) {
        int sum = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(size) AS \"totalsize\"" +
                    " FROM order_service.orders WHERE isWorking = ? && price = ? && sideIsBuy = ?");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, price);
            preparedStatement.setBoolean(3, isBuy);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                sum = resultSet.getInt("totalsize");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum;
    }

    @Override
    public int getHighestBuyPrice() {
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_service.orders " +
                    "WHERE isWorking = ? && sideIsBuy = ? ORDER BY price DESC LIMIT 1");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setBoolean(2, true);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result = resultSet.getInt("price");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int getLowestSellPrice() {
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM order_service.orders " +
                    "WHERE isWorking = ? && sideIsBuy = ? ORDER BY price LIMIT 1");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setBoolean(2, false);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result = resultSet.getInt("price");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
