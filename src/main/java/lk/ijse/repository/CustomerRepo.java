package lk.ijse.repository;

import javafx.collections.ObservableList;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRepo {
    private final Connection connection;

    public CustomerRepo() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
        if (this.connection == null) {
            throw new SQLException("Failed to establish a database connection");
        }
    }

    public boolean save(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (cusId, name, email, phone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customer.getCustomerId());
            pstm.setString(2, customer.getName());
            pstm.setString(3, customer.getEmail());
            pstm.setString(4, customer.getPhone());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET name=?, email=?, phone=? WHERE cusId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customer.getName());
            pstm.setString(2, customer.getEmail());
            pstm.setString(3, customer.getPhone());
            pstm.setString(4, customer.getCustomerId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(String customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE cusId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customerId);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Customer findCustomerById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE cusId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, customerId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getString("cusId"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone")
                    );
                }
            }
        }

        return null;
    }

    public void loadCustomers(ObservableList<Customer> customerList) throws SQLException {
        String sql = "SELECT * FROM customer";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("cusId"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone")
                );
                customerList.add(customer);
            }
        }
    }
}
