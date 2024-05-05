package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailRepo {
    private Connection connection;

    public OrderDetailRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean saveOrderProductDetail(List<OrderProductDetail> odList) throws SQLException {
        System.out.println("Saving order product detail information");
        for (OrderProductDetail od : odList) {
            System.out.println(od);
            String sql = "INSERT INTO orderProductDetails (orderId, productId, quantity, details) VALUES (?,?,?,?)";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, od.getOrderId());
            pst.setString(2, od.getProductId());
            pst.setInt(3, od.getQty());
            pst.setDouble(4, od.getDiscount());

            boolean isSaved = pst.executeUpdate() > 0;
            if(!isSaved) {
                return false;
            }
        }
        System.out.println(  "OrderProductDetails saved");
        return true;
    }


    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
