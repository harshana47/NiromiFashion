package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Order;
import lk.ijse.model.OrderProductDetail;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrderRepo {


    public static boolean saveOrder(Order order) throws SQLException {
        System.out.println("Saving order : "+order);
        String orderSql = "INSERT INTO orders (orderId, orderDate,totalAmount, cusId, paymentId , promoId, ExpireDiscountStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try  {
            DbConnection.getInstance().getConnection().setAutoCommit(false);


            PreparedStatement orderStatement = DbConnection.getInstance().getConnection().prepareStatement(orderSql);
            orderStatement.setString(1, order.getOrderId());
            orderStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now())); // Current date
            orderStatement.setBigDecimal(3, BigDecimal.valueOf(order.getTotalAmount()));
            orderStatement.setString(4, order.getCustomerId());
            orderStatement.setString(5, order.getPaymentId());
            orderStatement.setString(6, order.getPromoId());
            orderStatement.setString(7, order.getExpireDiscountStatus());
            int orderRowsAffected = orderStatement.executeUpdate();


            boolean isOrderSaved = orderRowsAffected > 0;
            if (!isOrderSaved) {
                DbConnection.getInstance().getConnection().rollback();
                return false;
            }
            System.out.println("isOrderSaved | "+isOrderSaved);

            OrderDetailRepo orderDetailRepo = new OrderDetailRepo();
            boolean isOrderDetailSaved = orderDetailRepo.saveOrderProductDetail(order.getOrderProductDetailList());
            if (!isOrderDetailSaved) {
                DbConnection.getInstance().getConnection().rollback();
                return false;
            }
            System.out.println("isOrderDetailedSaved | "+isOrderDetailSaved);

            //update product quantity
            ProductRepo productRepo = new ProductRepo();
            boolean isQtyUpdated = productRepo.updateQTY(order.getOrderProductDetailList());
            if (!isQtyUpdated) {
                DbConnection.getInstance().getConnection().rollback();
                return false;
            }
            System.out.println("isQtyUpdated | "+isQtyUpdated);

            DbConnection.getInstance().getConnection().commit();
            return true;

        }catch (Exception e){
            System.out.println("Transaction rolled back");
            DbConnection.getInstance().getConnection().rollback();
            throw e;
        }finally {
            DbConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }

    public String getNextOrderId() throws SQLException {
        String lastOrderId = getLastOrderId();
        int nextOrderNumber = Integer.parseInt(lastOrderId.substring(1)) + 1;
        return "O" + String.format("%04d", nextOrderNumber);
    }


    private String getLastOrderId() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String query = "SELECT orderId FROM orders ORDER BY orderId DESC LIMIT 1";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("orderId");
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
        }
        return "O0000"; //default
    }
}