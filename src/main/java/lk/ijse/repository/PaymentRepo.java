package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepo {
    private Connection connection;

    public PaymentRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean addPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payment (paymentId, method) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payment.getPaymentId());
            statement.setString(2, payment.getMethod());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePayment(Payment payment) throws SQLException {
        String sql = "DELETE FROM Payment WHERE paymentId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payment.getPaymentId());
            return statement.executeUpdate() > 0;
        }
    }

    public Payment searchPayment(String paymentId) throws SQLException {
        String sql = "SELECT * FROM Payment WHERE paymentId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Payment(
                            resultSet.getString("paymentId"),
                            resultSet.getString("method")
                    );
                }
            }
        }
        return null;
    }

    public String getPaymentIdByMethod(String paymentMethod) throws SQLException {
        String sql = "SELECT paymentId FROM payment WHERE method = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, paymentMethod);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("paymentId");
                }
            }
        }
        return null; // Return null if no payment ID found for the given payment method
    }
    public List<Payment> getAllPayment() throws SQLException {
        List<Payment> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Payment payment = new Payment(
                        resultSet.getString("paymentId"),
                        resultSet.getString("method")
                );
                paymentList.add(payment);
            }
        }
        return paymentList;
    }

    public boolean updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE Payment SET method=? WHERE paymentId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payment.getMethod());
            statement.setString(2, payment.getPaymentId());
            return statement.executeUpdate() > 0;
        }
    }

    public List<String> getAllPaymentMethods() throws SQLException {
        List<String> paymentMethods = new ArrayList<>();
        String sql = "SELECT method FROM Payment";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                paymentMethods.add(resultSet.getString("method"));
            }
        }
        return paymentMethods;
    }
}

