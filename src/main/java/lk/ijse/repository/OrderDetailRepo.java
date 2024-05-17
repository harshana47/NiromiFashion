package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailRepo {

    public boolean saveOrderProductDetail(List<OrderProductDetail> odList) {
        System.out.println("Saving order product detail information");
        Connection connection = null;
        PreparedStatement pst = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO orderProductDetails (orderId, productId, quantity, itemPrice, date) VALUES (?,?,?,?,?)";
            pst = connection.prepareStatement(sql);

            for (OrderProductDetail od : odList) {
                System.out.println(od);
                pst.setString(1, od.getOrderId());
                pst.setString(2, od.getProductId());
                pst.setInt(3, od.getQty());
                pst.setDouble(4, od.getTotal());
                pst.setObject(5, od.getOrderDate());

                boolean isSaved = pst.executeUpdate() > 0;
                if (!isSaved) {
                    return false;
                }
            }
            System.out.println("OrderProductDetails saved");
            return true;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

            return false;
        }
    }
    public List<OrderProductDetail> getAllOrderProductDetails() {
        List<OrderProductDetail> orderProductDetails = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM orderProductDetails";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OrderProductDetail orderProductDetail = new OrderProductDetail();
                orderProductDetail.setOrderId(resultSet.getString("orderId"));
                orderProductDetail.setProductId(resultSet.getString("productId"));
                orderProductDetail.setQty(resultSet.getInt("quantity"));
                orderProductDetail.setTotal(resultSet.getDouble("itemPrice"));
                orderProductDetail.setOrderDate(resultSet.getDate("date").toLocalDate());
                orderProductDetails.add(orderProductDetail);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

        }

        return orderProductDetails;
    }
}