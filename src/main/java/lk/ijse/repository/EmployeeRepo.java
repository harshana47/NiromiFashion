package lk.ijse.repository;

import javafx.scene.control.Alert;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepo {

    public boolean save(Employee employee) {
        String sql = "INSERT INTO employee (employeeId, name, depId, position, duty, email) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, employee.getEmployeeId());
            pstm.setString(2, employee.getName());
            pstm.setString(3, employee.getDepId());
            pstm.setString(4, employee.getPosition());
            pstm.setString(5, employee.getDuty());
            pstm.setString(6, employee.getEmail());

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                return increaseStaffCount(connection, employee.getDepId());
            }
            return false;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();
            return false;

        }
    }

    private boolean increaseStaffCount(Connection connection, String depId) throws SQLException {
        String updateSql = "UPDATE department SET staffCount = staffCount + 1 WHERE depId = ?";
        try (PreparedStatement updatePstm = connection.prepareStatement(updateSql)) {
            updatePstm.setString(1, depId);
            int updatedRows = updatePstm.executeUpdate();
            return updatedRows > 0;
        }
    }

    public boolean update(Employee employee) {
        String sql = "UPDATE employee SET name=?, depId=?, position=?, duty=?, email=? WHERE employeeId=?";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, employee.getName());
            pstm.setString(2, employee.getDepId());
            pstm.setString(3, employee.getPosition());
            pstm.setString(4, employee.getDuty());
            pstm.setString(5, employee.getEmail());
            pstm.setString(6, employee.getEmployeeId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return false;
        }
    }

    public Employee search(String employeeId) {
        String sql = "SELECT * FROM employee WHERE employeeId=?";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, employeeId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Employee(
                            resultSet.getString("employeeId"),
                            resultSet.getString("name"),
                            resultSet.getString("depId"),
                            resultSet.getString("position"),
                            resultSet.getString("duty"),
                            resultSet.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION,e.getMessage()).show();

            return null;
        }
    }

    public boolean delete(String employeeId) {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String depId = getDepartmentId(employeeId); // Get the department ID of the employee being deleted

            String sql = "DELETE FROM employee WHERE employeeId=?";
            pstm = connection.prepareStatement(sql);

            pstm.setString(1, employeeId);

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                // Decrease staff count in the department
                decreaseStaffCount( depId);
                return true; // Return true if deletion is successful
            }
            return false;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
            return false;
        }
    }


    private String getDepartmentId(String employeeId) throws SQLException {
        String sql = "SELECT depId FROM employee WHERE employeeId=?";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, employeeId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("depId");
                }
                return null;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

            return null;
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            pstm = connection.prepareStatement(sql);

            try (ResultSet resultSet = pstm.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = new Employee(
                            resultSet.getString("employeeId"),
                            resultSet.getString("name"),
                            resultSet.getString("depId"),
                            resultSet.getString("position"),
                            resultSet.getString("duty"),
                            resultSet.getString("email")
                    );
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();
        }
        return employees;
    }
    public void loadEmployees(List<Employee> employeeList) throws SQLException {
        employeeList.clear();
        employeeList.addAll(getAllEmployees());
    }

    public static int getEmployeeCount() {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM employee";
            pstm = connection.prepareStatement(sql);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Error getting employee count");
                }
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

            return -1;
        }
    }
    private boolean increaseStaffCount(String depId) {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE department SET staffCount = staffCount + 1 WHERE depId = ?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

            return false;
        }
    }
    private boolean decreaseStaffCount(String depId) {
        Connection connection = null;
        PreparedStatement pstm = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String sql = "UPDATE department SET staffCount = staffCount - 1 WHERE depId = ?";
            pstm = connection.prepareStatement(sql);
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

            return false;
        }
    }
    public List<String> getAllEmployeeEmails() {
        List<String> employeeEmails = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DbConnection.getInstance().getConnection();
            String query = "SELECT email FROM employee";
            preparedStatement = connection.prepareStatement(query);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String email = resultSet.getString("email");
                    employeeEmails.add(email);
                }
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.CONFIRMATION, e.getMessage()).show();

        }

        return employeeEmails;
    }
}