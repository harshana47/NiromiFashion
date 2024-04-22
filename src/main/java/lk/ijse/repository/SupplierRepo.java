package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepo {
    private Connection connection;

    public SupplierRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO supplier (supplierId, name, address, contact, email) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getSupplierId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getAddress());
            statement.setString(4, supplier.getContact());
            statement.setString(5, supplier.getEmail());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSupplier(String supplierId) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplierId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE supplier SET name=?, address=?, contact=?, email=? WHERE supplierId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getAddress());
            statement.setString(3, supplier.getContact());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getSupplierId());

            return statement.executeUpdate() > 0;
        }
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> supplierList = new ArrayList<>();
        String sql = "SELECT * FROM supplier";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Supplier supplier = new Supplier(
                        resultSet.getString("supplierId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
                supplierList.add(supplier);
            }
        }
        return supplierList;
    }
    public Supplier searchSupplier(String supplierId) throws SQLException {
        String sql = "SELECT * FROM supplier WHERE supplierId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Supplier(
                            resultSet.getString("supplierId"),
                            resultSet.getString("name"),
                            resultSet.getString("address"),
                            resultSet.getString("contact"),
                            resultSet.getString("email")
                    );
                }
            }
        }
        return null;
    }


    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
