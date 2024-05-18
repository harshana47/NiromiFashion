package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Customer;
import lk.ijse.model.Promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepo {

    public boolean save(Promotion promotion) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO promotion (promoId, promoName, discountPercentage) VALUES (?, ?, ?)";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, promotion.getPromoId());
            pstm.setString(2, promotion.getPromoName());
            pstm.setString(3, promotion.getDiscountPercentage());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            throw e;
        }
    }
    public boolean update(Promotion promotion) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE promotion SET promoName=?, discountPercentage=? WHERE promoId=?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, promotion.getPromoName());
            pstm.setString(2, promotion.getDiscountPercentage());
            pstm.setString(3, promotion.getPromoId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
        }
        return false;
    }

    public boolean delete(String promoId) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "DELETE FROM promotion WHERE promoId=?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, promoId);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return false; //deletion failed
        }
    }

    public Promotion search(String promoId) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM promotion WHERE promoId=?";
            pstm = connection.prepareStatement(sql);
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
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return null; // Return null to indicate search failed
        }
    }


    public List<Promotion> getAllPromotions() throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        List<Promotion> promotions = new ArrayList<>();
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM promotion";
            pstm = connection.prepareStatement(sql);
            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                Promotion promotion = new Promotion(
                        resultSet.getString("promoId"),
                        resultSet.getString("promoName"),
                        resultSet.getString("discountPercentage")
                );
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
        }
        return promotions;
    }

    public List<String> getAllPromoNames() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<String> promoNames = new ArrayList<>();
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT promoName FROM promotion";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                promoNames.add(resultSet.getString("promoName"));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
        }
        return promoNames;
    }

    public Promotion findPromotionById(String promoId) throws SQLException {
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM promotion WHERE promoId=?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, promoId);

            resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                return new Promotion(
                        resultSet.getString("promoId"),
                        resultSet.getString("promoName"),
                        resultSet.getString("discountPercentage")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return null;
        }
    }


    public String findPromotionByName(String promoName) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT promoId FROM promotion WHERE promoName = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, promoName);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("promoId");
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
        }
        return null;
    }
}