package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM user";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userId = resultSet.getString("userId");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String phone = resultSet.getString("phone");

                User user = new User(userId, name, password, phone);
                userList.add(user);
            }
            return userList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserById(String userId) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM User WHERE userId = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, userId);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String phone = resultSet.getString("phone");
                return new User(userId, name, password, phone);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean saveUser(String userId, String name, String password, String phone) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO User VALUES (?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUser(String userId, String name, String password, String phone) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE User SET name = ?, password = ?, phone = ? WHERE userId = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.setString(3, phone);
            pstmt.setString(4, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteUser(String userId) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "DELETE FROM User WHERE userId = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String findUserByPhone(String phone) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM User WHERE phone =?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, phone);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("userId");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean updatePassword(String userId , String password) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE User SET password = ? WHERE userId = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, password);
            pstmt.setString(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
