package lk.ijse.repository;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentRepo {


    public boolean addDepartment(String depId, String name, int staffCount) {
        Connection connection = null;
        PreparedStatement pstm = null;

        String sql = "INSERT INTO department (depId, name, staffCount) VALUES (?, ?, ?)";

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, depId);
            pstm.setString(2, name);
            pstm.setInt(3, staffCount);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return false;
        }
    }


    public boolean deleteDepartment(String depId) {
        Connection connection = null;
        PreparedStatement pstm = null;

        String sql = "DELETE FROM department WHERE depId = ?";

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return false;
        }
    }


    public Department searchDepartment(String depId) {
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM department WHERE depId = ?";
        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, depId);
            resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int staffCount = resultSet.getInt("staffCount");
                return new Department(depId, name, staffCount);
            } else {
                return null; // Department not found
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return null;
        }
    }


    public boolean updateDepartment(Department department) {
        Connection connection = null;
        PreparedStatement pstm = null;

        String sql = "UPDATE department SET name = ?, staffCount = ? WHERE depId = ?";

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, department.getName());
            pstm.setInt(2, department.getStaffCount());
            pstm.setString(3, department.getDepId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return false;
        }
    }


    public void loadDepartments(ObservableList<Department> departmentList) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM department";

        try {
            connection = DbConnection.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            departmentList.clear();

            while (resultSet.next()) {
                String depId = resultSet.getString("depId");
                String name = resultSet.getString("name");
                int staffCount = resultSet.getInt("staffCount");

                Department department = new Department(depId, name, staffCount);
                departmentList.add(department);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

        }
    }
}