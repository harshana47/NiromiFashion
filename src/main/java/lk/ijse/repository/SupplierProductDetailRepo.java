package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.SupplierProductDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SupplierProductDetailRepo {
    private Connection connection;

    public SupplierProductDetailRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addSupplierProductDetail(SupplierProductDetail supplierProductDetail) throws SQLException {
        String sql = "INSERT INTO supplierProductDetails ( productId, supplierId) " +
                "VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierProductDetail.getProductId());
            statement.setString(2, supplierProductDetail.getSupplierId());

            return statement.executeUpdate() > 0;
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
