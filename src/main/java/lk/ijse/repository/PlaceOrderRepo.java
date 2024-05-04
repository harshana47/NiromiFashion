package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.PlaceOrder;
import java.sql.Connection;
import java.sql.SQLException;

public class PlaceOrderRepo {
    public static boolean placeOrder(PlaceOrder po) throws SQLException {
        System.out.println("placeOrder ");
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try {
            boolean isOrderSaved = OrderRepo.saveOrder(po.getOrder());
            System.out.println("isOrderSaved "+ isOrderSaved);
            if (isOrderSaved) {
                boolean isQtyUpdated = ProductRepo.update(po.getOdList());
                if (isQtyUpdated) {
                    System.out.println("isQtyUpdated | "+isQtyUpdated);
                    OrderDetailRepo orderDetailRepo = new OrderDetailRepo();
                    boolean isOrderDetailSaved = orderDetailRepo.save(po.getOdList(), po.getOrder().getOrderId());
                    if (isOrderDetailSaved) {
                        System.out.println("isOrderDetailedSaved | "+isOrderDetailSaved);
                        connection.commit();
                        return true; // Order placement successful
                    }
                }
            }
            throw new SQLException("Transaction failed: One or more operations were unsuccessful.");
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            return false; // Order placement unsuccessful
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
