package lk.ijse.repository;

import javafx.collections.ObservableList;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentRepo {
    private Connection connection;

    public DepartmentRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean addDepartment(String depId, String name, int staffCount) throws SQLException {
        String sql = "INSERT INTO department (depId, name, staffCount) VALUES (?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, depId);
            pstm.setString(2, name);
            pstm.setInt(3, staffCount);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteDepartment(String depId) throws SQLException {
        String sql = "DELETE FROM department WHERE depId = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Department searchDepartment(String depId) throws SQLException {
        String sql = "SELECT * FROM department WHERE depId = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, depId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int staffCount = resultSet.getInt("staffCount");
                    return new Department(depId, name, staffCount);
                }
                return null; // Department not found
            }
        }
    }

    public boolean updateDepartment(Department department) throws SQLException {
        String sql = "UPDATE department SET name = ?, staffCount = ? WHERE depId = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, department.getName());
            pstm.setInt(2, department.getStaffCount());
            pstm.setString(3, department.getDepId());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public void loadDepartments(ObservableList<Department> departmentList) throws SQLException {
        String sql = "SELECT * FROM department";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            departmentList.clear(); // Clear existing data

            while (resultSet.next()) {
                String depId = resultSet.getString("depId");
                String name = resultSet.getString("name");
                int staffCount = resultSet.getInt("staffCount");

                Department department = new Department(depId, name, staffCount);
                departmentList.add(department);
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
