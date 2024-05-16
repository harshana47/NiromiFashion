package lk.ijse.repository;

import javafx.scene.control.Alert;
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


    public boolean addProduct(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO product (productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getExpireDate());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQtyOnHand());
            statement.setString(6, product.getEmployeeId());
            statement.setString(7, product.getPromoId());
            statement.setString(8, product.getSupplierName());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return false;
        }
    }

    public boolean deleteProduct(String productId) {
        Connection connection=null;
        String deleteSupplierProductDetailsSql = "DELETE FROM supplierProductDetails WHERE productId=?";
        String deleteProductSql = "DELETE FROM product WHERE productId=?";

        try {
            connection =DbConnection.getInstance().getConnection();
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




    public Product searchProduct(String productId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM product WHERE productId=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, productId);

            resultSet = statement.executeQuery();
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
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();


        }

        return null; // Return null if product not found or an error occurred
    }

    public boolean updateProduct(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE product SET name=?, expireDate=?, price=?, qtyOnHand=?, employeeId=?, promoId=?, supplierName=? WHERE productId=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getExpireDate());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQtyOnHand());
            statement.setString(5, product.getEmployeeId());
            statement.setString(6, product.getPromoId());
            statement.setString(7, product.getSupplierName());
            statement.setString(8, product.getProductId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return false; // Return false to indicate update failure
        }
    }
    public static boolean updateQTY(List<OrderProductDetail> odList) throws SQLException {

        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println("Update Product: " + odList.size());

        for (OrderProductDetail product : odList) {
            System.out.println("updateQty: " + product.getProductId() + " qtyOnHand=" + product.getQty());
            String sql = "UPDATE product SET qtyOnHand = qtyOnHand - ? WHERE productId = ?";

            try {
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setInt(1, product.getQty());
                pstm.setString(2, product.getProductId());

                int rowsUpdated = pstm.executeUpdate();
                if (rowsUpdated <= 0) {
                    return false;  // Return false if no rows were updated
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            }
            return true;  // All updates were successful
        }

        return false;  // Return false on any exception
    }
    public List<Product> getAllProducts () {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM product";

        try {
            Connection connection = DbConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

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
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

        }

        return productList;
    }


    public LocalDate getProductExpirationDate(String productId) throws SQLException {
        String sql = "SELECT expireDate FROM product WHERE productId=?";
        try {
            Connection connection = DbConnection.getInstance().getConnection();

            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, productId);

            try {ResultSet resultSet = pstm.executeQuery();
                if (resultSet.next()) {
                    return LocalDate.parse(resultSet.getString("expireDate"));
                }
            }catch (SQLException e){
                new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            }
        } catch (SQLException  e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }
        return null;  // Return null if no expiration date found or on any exception
    }

    public Product findProductById(String productId) {
        String sql = "SELECT * FROM product WHERE productId=?";
        try {Connection connection = DbConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, productId);

            try {ResultSet resultSet = pstm.executeQuery();
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
            }catch (SQLException e) {
                new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }
        return null;  // Return null if product not found or on any exception
    }

    public List<Product> getExpiringProducts(LocalDate thresholdDate) {
        List<Product> expiringProducts = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE expireDate <= ?";
        try {Connection connection = DbConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setObject(1, thresholdDate);
            try {ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Product product = new Product(
                            resultSet.getString("productId"),
                            resultSet.getString("name"),
                            resultSet.getString("expireDate"), // Assuming expireDate is a String
                            resultSet.getDouble("price"),
                            resultSet.getInt("qtyOnHand"),
                            resultSet.getString("employeeId"),
                            resultSet.getString("promoId"),
                            resultSet.getString("supplierName")
                    );
                    expiringProducts.add(product);
                }
            }catch (SQLException e) {
                new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }
        return expiringProducts;
    }
}