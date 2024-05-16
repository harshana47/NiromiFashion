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

    public boolean deleteProduct(String productId) {
        String deleteSupplierProductDetailsSql = "DELETE FROM supplierProductDetails WHERE productId=?";
        String deleteProductSql = "DELETE FROM product WHERE productId=?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteSupplierProductDetailsStmt = connection.prepareStatement(deleteSupplierProductDetailsSql)) {
                deleteSupplierProductDetailsStmt.setString(1, productId);
                int rowsAffected = deleteSupplierProductDetailsStmt.executeUpdate();

                if (rowsAffected > 0) {
                    // If deletion from supplierProductDetails was successful, commit the transaction
                    connection.commit();
                    return true; // Deletion successful from supplierProductDetails
                } else {
                    // If productId is not found in supplierProductDetails, delete from product table
                    try (PreparedStatement deleteProductStmt = connection.prepareStatement(deleteProductSql)) {
                        deleteProductStmt.setString(1, productId);
                        rowsAffected = deleteProductStmt.executeUpdate();

                        // Commit the transaction if deletion from product is successful
                        if (rowsAffected > 0) {
                            connection.commit();
                            return true; // Deletion successful from product
                        }
                    }
                }

                // Rollback the transaction if any deletion fails
                connection.rollback();
                return false;
            } catch (SQLException e) {
                // Rollback the transaction in case of an exception
                connection.rollback();
                throw new RuntimeException("Error deleting product: " + e.getMessage(), e);
            } finally {
                // Reset auto-commit to true after completing the transaction
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
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
    public List<Product> getExpiringProducts(LocalDate thresholdDate) throws SQLException {
        List<Product> expiringProducts = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE expireDate <= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, thresholdDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductId(resultSet.getString("productId"));
                    product.setName(resultSet.getString("name"));
                    product.setExpireDate(String.valueOf(resultSet.getObject("expireDate", LocalDate.class)));
                    // Set other properties as needed
                    expiringProducts.add(product);
                }
            }
        }
        return expiringProducts;
    }

}
