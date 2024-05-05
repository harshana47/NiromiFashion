package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;
import lk.ijse.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {
    private Connection connection;

    public ProductRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO product (productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getExpireDate());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQtyOnHand());
            statement.setString(6, product.getEmployeeId());
            statement.setString(7, product.getPromoId());
            statement.setString(8, product.getSupplierName());

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
                            resultSet.getString("employeeId"),
                            resultSet.getString("promoId"),
                            resultSet.getString("supplierName")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET name=?, expireDate=?, price=?, qtyOnHand=?, employeeId=?, promoId=?,supplierName=? WHERE productId=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getExpireDate());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQtyOnHand());
            statement.setString(5, product.getEmployeeId());
            statement.setString(6, product.getPromoId());
            statement.setString(7, product.getSupplierName());
            statement.setString(8, product.getProductId());

            return statement.executeUpdate() > 0;
        }
    }
    public static boolean updateQTY(List<OrderProductDetail> odList) throws SQLException {
        System.out.println("Update Product"+ odList.size());

        for (OrderProductDetail product : odList) {
            System.out.println("updateQty: " + product.getProductId()+" qtyOnHand="+product.getQty());
            String sql = "UPDATE product SET qtyOnHand = qtyOnHand - ? WHERE productId = ?";

            PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setObject(1, product.getQty());
            pstm.setObject(2, product.getProductId());

            boolean isUpdated = pstm.executeUpdate() > 0;
            if (!isUpdated) {
                return false;
            }
        }
        return true;
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
                        resultSet.getString("employeeId"),
                        resultSet.getString("promoId"),
                        resultSet.getString("supplierName")
                );
                productList.add(product);
            }
        }
        return productList;
    }


    public LocalDate getProductExpirationDate(String productId) throws SQLException {
        String sql = "SELECT expireDate FROM product WHERE productId=?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, productId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return LocalDate.parse(resultSet.getString("expireDate"));
                } else {
                    return null;
                }
            }
        }
    }

    public Product findProductById(String productId) throws SQLException {
        String sql = "SELECT * FROM product WHERE productId=?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, productId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getString("productId"),
                            resultSet.getString("name"),
                            resultSet.getString("expireDate"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("qtyOnHand"),
                            resultSet.getString("employeeId"),
                            resultSet.getString("promoId"),
                            resultSet.getString("supplierName")
                    );
                } else {
                    return null;
                }
            }
        }
    }
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}
