package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Order;
import lk.ijse.model.OrderProductDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderRepo {
    private final Connection connection;

    public OrderRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public void saveOrder(Order order) throws SQLException {
        if (order == null || order.getOrderItems() == null) {
            throw new IllegalArgumentException("Order or order items cannot be null");
        }

        String orderSql = "INSERT INTO orders (orderId, orderDate, totalAmount, cusId, paymentId, promoId, ExpireDiscountStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String orderProductSql = "INSERT INTO orderProductDetails (orderId, productId, details) VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
                orderStatement.setString(1, order.getOrderId());
                orderStatement.setDate(2, java.sql.Date.valueOf(order.getOrderDate()));
                orderStatement.setBigDecimal(3, BigDecimal.valueOf(order.getTotalAmount()));
                orderStatement.setString(4, order.getCustomerId());
                orderStatement.setString(5, order.getPaymentId());
                orderStatement.setString(6, order.getPromoId());
                orderStatement.setString(7, order.getExpireDiscountStatus());

                int orderRowsAffected = orderStatement.executeUpdate();

                if (orderRowsAffected > 0) {
                    // Save order-product details
                    List<OrderProductDetail> orderItems = order.getOrderItems();
                    for (OrderProductDetail item : orderItems) {
                        if (item != null && item.getProductId() != null && item.getDetails() != null) {
                            try (PreparedStatement orderProductStatement = connection.prepareStatement(orderProductSql)) {
                                orderProductStatement.setString(1, order.getOrderId());
                                orderProductStatement.setString(2, item.getProductId());
                                orderProductStatement.setString(3, item.getDetails());

                                int orderProductRowsAffected = orderProductStatement.executeUpdate();

                                if (orderProductRowsAffected <= 0) {
                                    throw new SQLException("Failed to save order product details");
                                }
                            }
                        } else {
                            throw new IllegalArgumentException("OrderProductDetail, productId, or details is null");
                        }
                    }

                    connection.commit();
                } else {
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
