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
    private static Connection connection;

    public SupplierRepo() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
    }

    // Ensures that the connection is open before using it
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DbConnection.getInstance().getConnection();
        }
        return connection;
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO supplier (supplierId, name, address, contact, email) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, supplier.getSupplierId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getAddress());
            statement.setString(4, supplier.getContact());
            statement.setString(5, supplier.getEmail());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSupplier(String supplierId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM supplier WHERE supplierId=?")) {
            statement.setString(1, supplierId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE supplier SET name=?, address=?, contact=?, email=? WHERE supplierId=?")) {
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
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM supplier");
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
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM supplier WHERE supplierId=?")) {
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

    public static List<String> getAllSupplierNames() throws SQLException {
        List<String> supplierNames = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            if (connection != null) {
                String sql = "SELECT name FROM supplier";
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    supplierNames.add(name);
                }
            }
        } finally {

        }

        return supplierNames;
    }

    public static String getSupplierIdByName(String supplierName) throws SQLException {
        String sql = "SELECT supplierId FROM supplier WHERE name=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("supplierId");
                }
            }
        }
        return null; // Supplier ID not found
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
