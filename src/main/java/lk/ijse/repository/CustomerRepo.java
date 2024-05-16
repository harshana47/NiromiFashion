package lk.ijse.repository;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRepo {


    public boolean save(Customer customer) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();

            String sql = "INSERT INTO customer (cusId, name, email, phone) VALUES (?, ?, ?, ?)";
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, customer.getCustomerId());
            pstm.setString(2, customer.getName());
            pstm.setString(3, customer.getEmail());
            pstm.setString(4, customer.getPhone());

            int affectedRows = pstm.executeUpdate();


            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean update(Customer customer) {
        String sql = "UPDATE customer SET name=?, email=?, phone=? WHERE cusId=?";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, customer.getName());
            pstm.setString(2, customer.getEmail());
            pstm.setString(3, customer.getPhone());
            pstm.setString(4, customer.getCustomerId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return false;
        }
    }



    public boolean delete(String customerId) {
        Connection connection = null;
        PreparedStatement pstm = null;

        String sql = "DELETE FROM customer WHERE cusId=?";

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, customerId);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return false;
        }
    }
    public Customer findCustomerById(String id) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;


        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM customer WHERE cusId=?";
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, id);

            try {
                ResultSet resultSet = pstm.executeQuery();

                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getString("cusId"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone")
                    );
                }
            }catch (SQLException e){
                new Alert(Alert.AlertType.CONFIRMATION,e.getMessage());
            }
        }catch (SQLException e){
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage());
        }


        return null;
    }

    public Customer findCustomerByPhone(String phone) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;



        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM customer WHERE phone=?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, phone);

            try {
                ResultSet resultSet = pstm.executeQuery();
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getString("cusId"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone")
                    );
                }
            }catch (SQLException e){
                new Alert(Alert.AlertType.CONFIRMATION,e.getMessage());
            }
        }catch (SQLException e){
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage());
        }


        return null;
    }


    public void loadCustomers(ObservableList<Customer> customerList) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM customer";
            pstm = connection.prepareStatement(sql);

            ResultSet resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("cusId"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone")
                );
                customerList.add(customer);
            }
        }catch (SQLException e){
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage());
        }
    }
}