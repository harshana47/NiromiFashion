package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;

import java.sql.*;
import java.util.ArrayList;
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
            String sql = "INSERT INTO orderProductDetails (orderId, productId, quantity,itemPrice, date) VALUES (?,?,?,?,?)";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, od.getOrderId());
            pst.setString(2, od.getProductId());
            pst.setInt(3, od.getQty());
            pst.setDouble(4, od.getTotal());
            pst.setObject(5, od.getOrderDate()); // Assuming orderDate is of type LocalDate

            boolean isSaved = pst.executeUpdate() > 0;
            if(!isSaved) {
                return false;
            }
        }
        System.out.println("OrderProductDetails saved");
        return true;
    }

    public List<OrderProductDetail> getAllOrderProductDetails() throws SQLException {
        List<OrderProductDetail> orderProductDetails = new ArrayList<>();
        String sql = "SELECT * FROM orderProductDetails";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                OrderProductDetail orderProductDetail = new OrderProductDetail();
                orderProductDetail.setOrderId(resultSet.getString("orderId"));
                orderProductDetail.setProductId(resultSet.getString("productId"));
                orderProductDetail.setQty(resultSet.getInt("quantity"));
                orderProductDetail.setTotal(resultSet.getDouble("itemPrice"));
                orderProductDetail.setOrderDate(resultSet.getDate("date").toLocalDate());
                orderProductDetails.add(orderProductDetail);
            }
        }

        return orderProductDetails;
    }
}
