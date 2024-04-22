package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepo {
    private final Connection connection;

    public PromotionRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean save(Promotion promotion) throws SQLException {
        String sql = "INSERT INTO promotion (promoId, promoName, discountPercentage) VALUES (?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, promotion.getPromoId());
            pstm.setString(2, promotion.getPromoName());
            pstm.setString(3, promotion.getDiscountPercentage());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean update(Promotion promotion) throws SQLException {
        String sql = "UPDATE promotion SET promoName=?, discountPercentage=? WHERE promoId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, promotion.getPromoName());
            pstm.setString(2, promotion.getDiscountPercentage());
            pstm.setString(3, promotion.getPromoId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(String promoId) throws SQLException {
        String sql = "DELETE FROM promotion WHERE promoId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, promoId);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Promotion search(String promoId) throws SQLException {
        String sql = "SELECT * FROM promotion WHERE promoId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, promoId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Promotion(
                            resultSet.getString("promoId"),
                            resultSet.getString("promoName"),
                            resultSet.getString("discountPercentage")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public List<Promotion> getAllPromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotion";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {

            while (resultSet.next()) {
                Promotion promotion = new Promotion(
                        resultSet.getString("promoId"),
                        resultSet.getString("promoName"),
                        resultSet.getString("discountPercentage")
                );
                promotions.add(promotion);
            }
        }

        return promotions;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
