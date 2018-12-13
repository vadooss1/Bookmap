package exchangetask;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static exchangetask.ConfigLoader.passwordDB;
import static exchangetask.ConfigLoader.urlDB;
import static exchangetask.ConfigLoader.userDB;


public class MySQLconnector {
    final static Logger logger = Logger.getLogger(MySQLconnector.class);

    public MySQLconnector(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            logger.error("Invalid driver", e);
        }
    }

    public Connection getConnection(){
        Connection connection=null;
        try{
         connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
        }catch(SQLException e){
            logger.error("Cannot plug in...", e);
        }
        return connection;
    }

    public void resetDB(Connection connection){
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP SCHEMA IF EXISTS `order_service`");
            statement.execute("CREATE SCHEMA IF NOT EXISTS `order_service` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin");
            statement.execute("USE `order_service`");
            statement.execute("CREATE TABLE IF NOT EXISTS `order_service`.`orders` (\n" +
                    "  `orderId` BIGINT(16) NOT NULL,\n" +
                    "  `sideIsBuy` TINYINT NULL DEFAULT 0,\n" +
                    "  `price` DECIMAL(10,2) NULL,\n" +
                    "  `size` INT NULL,\n" +
                    "  `isWorking` TINYINT NULL DEFAULT 0,\n" +
                    "  PRIMARY KEY (`orderId`),\n" +
                    "  UNIQUE INDEX `orderId_UNIQUE` (`orderId` ASC))\n" +
                    "ENGINE = InnoDB\n" +
                    "DEFAULT CHARACTER SET = utf8\n" +
                    "COLLATE = utf8_bin");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
