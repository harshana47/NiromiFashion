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

    public boolean save(List<OrderProductDetail> odList, String orderId) throws SQLException {
        try {
            for (OrderProductDetail od : odList) {
                saveOrderProductDetail(od, orderId);
            }
            return true; // All details saved successfully
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException("Failed to save order product details: " + e.getMessage());

        }
    }

    public void saveOrderProductDetail(OrderProductDetail orderProductDetail, String orderId) throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Connection is null or closed.");
            }

            String sql = "INSERT INTO orderProductDetails (orderId, productId, quantity, itemPrice) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, orderId);
            preparedStatement.setString(2, orderProductDetail.getProductId());
            preparedStatement.setInt(3, orderProductDetail.getQty());
            preparedStatement.setDouble(4, orderProductDetail.getPrice());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error saving order product detail: " + e.getMessage());
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
