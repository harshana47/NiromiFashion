package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Supplier;
import lk.ijse.model.SupplierProductDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepo {


    public boolean addSupplier(Supplier supplier) throws SQLException {
        Connection connection = null;
        PreparedStatement supplierStatement = null;


        try {
            connection = DbConnection.getInstance().getConnection();
            String supplierSql = ("INSERT INTO supplier (supplierId, name, address, contact, email) VALUES (?, ?, ?, ?, ?)");
            supplierStatement = connection.prepareStatement(supplierSql);
            DbConnection.getInstance().getConnection().setAutoCommit(false);


            supplierStatement.setString(1, supplier.getSupplierId());
            supplierStatement.setString(2, supplier.getName());
            supplierStatement.setString(3, supplier.getAddress());
            supplierStatement.setString(4, supplier.getContact());
            supplierStatement.setString(5, supplier.getEmail());

            int supplierRowsAffected = supplierStatement.executeUpdate();

            boolean isSupplierSaved = supplierRowsAffected > 0;
            if (!isSupplierSaved) {
                DbConnection.getInstance().getConnection().rollback();
                return false;
            }
            System.out.println("isSupplierSaved  | " + isSupplierSaved);

            SupplierProductDetailRepo supplierProductDetailRepo = new SupplierProductDetailRepo();
            boolean isSupplierProductSaved = true;
            for (SupplierProductDetail productDetail : supplier.getSupplierProductDetailList()) {
                isSupplierProductSaved &= supplierProductDetailRepo.addSupplierProductDetail(productDetail);
            }
            if (!isSupplierProductSaved) {
                DbConnection.getInstance().getConnection().rollback();
                return false;
            }
            System.out.println("isSupplierProductSaved | " + isSupplierProductSaved);

            DbConnection.getInstance().getConnection().commit();
            return true;
        } catch (Exception e) {
            System.out.println("Transaction rolled back");
            DbConnection.getInstance().getConnection().rollback();
            throw e;
        } finally {
            DbConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }


    public boolean deleteSupplier(String supplierId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;


        connection = DbConnection.getInstance().getConnection();

        statement = connection.prepareStatement("DELETE FROM supplier WHERE supplierId=?");
        statement.setString(1, supplierId);
        return statement.executeUpdate() > 0;
    }


    public boolean updateSupplier(Supplier supplier) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DbConnection.getInstance().getConnection();

            statement = connection.prepareStatement("UPDATE supplier SET name=?, address=?, contact=?, email=? WHERE supplierId=?");
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getAddress());
            statement.setString(3, supplier.getContact());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getSupplierId());

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> supplierList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DbConnection.getInstance().getConnection();

            statement = conn.prepareStatement("SELECT * FROM supplier");
            resultSet = statement.executeQuery();

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
        }catch (SQLException e){
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
        }
        return supplierList;
    }

    public Supplier searchSupplier(String supplierId) throws SQLException {
        Connection conn = null;
        PreparedStatement statement = null;
        Supplier supplier = null;
        ResultSet resultSet = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            statement = conn.prepareStatement("SELECT * FROM supplier WHERE supplierId=?");
            statement.setString(1, supplierId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                supplier = new Supplier(
                        resultSet.getString("supplierId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return supplier;
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
        } catch (SQLException e){
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
        }

        return supplierNames;
    }

    public static String getSupplierIdByName(String supplierName) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String supplierId = null;

        try {
            connection = DbConnection.getInstance().getConnection();

            String sql = "SELECT supplierId FROM supplier WHERE name=?";
            statement = connection.prepareStatement(sql);

            statement.setString(1, supplierName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                supplierId = resultSet.getString("supplierId");
            } else {
                throw new SQLException("Supplier ID not found for the given supplier name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return supplierId;
    }
}