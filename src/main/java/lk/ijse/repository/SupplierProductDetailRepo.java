package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.SupplierProductDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SupplierProductDetailRepo {

    public boolean addSupplierProductDetail(SupplierProductDetail supplierProductDetail) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO supplierProductDetails (productId, supplierId) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, supplierProductDetail.getProductId());
            statement.setString(2, supplierProductDetail.getSupplierId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return false;
        }
    }
}