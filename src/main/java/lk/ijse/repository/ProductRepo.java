package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {
    private Connection connection;

    public ProductRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO product (productId, name, expireDate, price, qtyOnHand, employeeId) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getExpireDate());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQtyOnHand());
            statement.setString(6, product.getEmployeeId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(String productId) throws SQLException {
        String sql = "DELETE FROM product WHERE productId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            return statement.executeUpdate() > 0;
        }
    }

    public Product searchProduct(String productId) throws SQLException {
        String sql = "SELECT * FROM product WHERE productId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getString("productId"),
                            resultSet.getString("name"),
                            resultSet.getString("expireDate"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("qtyOnHand"),
                            resultSet.getString("employeeId")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET name=?, expireDate=?, price=?, qtyOnHand=?, employeeId=? WHERE productId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getExpireDate());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQtyOnHand());
            statement.setString(5, product.getEmployeeId());
            statement.setString(6, product.getProductId());

            return statement.executeUpdate() > 0;
        }
    }

    public void loadProducts(List<Product> productList) throws SQLException {
        productList.clear();
        productList.addAll(getAllProducts());
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getString("productId"),
                        resultSet.getString("name"),
                        resultSet.getString("expireDate"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("qtyOnHand"),
                        resultSet.getString("employeeId")
                );
                productList.add(product);
            }
        }
        return productList;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
