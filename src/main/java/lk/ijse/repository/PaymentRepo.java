package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepo {
    public boolean addPayment(Payment payment) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO Payment (paymentId, method) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, payment.getPaymentId());
            statement.setString(2, payment.getMethod());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return false;
        }
    }

    public boolean deletePayment(Payment payment) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "DELETE FROM Payment WHERE paymentId=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, payment.getPaymentId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return false;
        }
    }
    public Payment searchPayment(String paymentId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM Payment WHERE paymentId=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, paymentId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Payment(
                        resultSet.getString("paymentId"),
                        resultSet.getString("method")
                );
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }

        return null;
    }

    public String getPaymentIdByMethod(String paymentMethod) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT paymentId FROM payment WHERE method = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, paymentMethod);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("paymentId");
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }

        return null;
    }
    public List<Payment> getAllPayment() {
        List<Payment> paymentList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM payment";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Payment payment = new Payment(
                        resultSet.getString("paymentId"),
                        resultSet.getString("method")
                );
                paymentList.add(payment);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }

        return paymentList;
    }

    public boolean updatePayment(Payment payment) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE Payment SET method=? WHERE paymentId=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, payment.getMethod());
            statement.setString(2, payment.getPaymentId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return false;
        }
    }

    public List<String> getAllPaymentMethods() {
        List<String> paymentMethods = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT method FROM Payment";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                paymentMethods.add(resultSet.getString("method"));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }
        return paymentMethods;
    }
}