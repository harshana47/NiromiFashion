package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderRepo {
    private final Connection connection;

    public OrderRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addButton(Order order) throws SQLException {
        String sql = "INSERT INTO orders (orderId, orderDate, totalAmount, cusId, paymentId, promoId,ExpireDiscountStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, order.getOrderId());
            pstm.setDate(2, java.sql.Date.valueOf(order.getOrderDate())); // Assuming orderDate is a LocalDate
            pstm.setDouble(3, order.getTotalAmount());
            pstm.setString(4, order.getCustomerId());
            pstm.setString(5, order.getPaymentId());
            pstm.setString(6, order.getPromoId());
            pstm.setString(7,order.getExpireDiscountStatus());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean saveOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (orderId, orderDate, totalAmount, cusId, paymentId, promoId,ExpireDiscountStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, order.getOrderId());
            pstm.setDate(2, java.sql.Date.valueOf(order.getOrderDate())); // Assuming orderDate is a LocalDate
            pstm.setDouble(3, order.getTotalAmount());
            pstm.setString(4, order.getCustomerId());
            pstm.setString(5, order.getPaymentId());
            pstm.setString(6, order.getPromoId());
            pstm.setString(7,order.getExpireDiscountStatus());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public void deleteOrder(String orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE orderId=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orderId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("Failed to delete order.");
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
