package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;
import lk.ijse.model.Promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesRepo {
    public static OrderProductDetail searchSale(String orderId) {
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM orderProductDetails WHERE orderId=?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, orderId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new OrderProductDetail(
                            resultSet.getString("orderId"),
                            resultSet.getString("productId"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("itemPrice"),
                            resultSet.getDate("date").toLocalDate()
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return null;
        }
    }
}
