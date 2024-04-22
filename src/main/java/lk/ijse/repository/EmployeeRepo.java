package lk.ijse.repository;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepo {
    private static Connection connection;

    public EmployeeRepo() throws SQLException {
        connection = DbConnection.getInstance().getConnection();
    }

    public boolean save(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (employeeId, name, depId, position, duty) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, employee.getEmployeeId());
            pstm.setString(2, employee.getName());
            pstm.setString(3, employee.getDepId());
            pstm.setString(4, employee.getPosition());
            pstm.setString(5, employee.getDuty());

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                // Increase staff count in the department
                return increaseStaffCount(employee.getDepId());
            }
            return false;
        }
    }

    public boolean update(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET name=?, depId=?, position=?, duty=? WHERE employeeId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, employee.getName());
            pstm.setString(2, employee.getDepId());
            pstm.setString(3, employee.getPosition());
            pstm.setString(4, employee.getDuty());
            pstm.setString(5, employee.getEmployeeId());

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Employee search(String employeeId) throws SQLException {
        String sql = "SELECT * FROM employee WHERE employeeId=?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, employeeId);

            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Employee(
                            resultSet.getString("employeeId"),
                            resultSet.getString("name"),
                            resultSet.getString("depId"),
                            resultSet.getString("position"),
                            resultSet.getString("duty")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public boolean delete(String employeeId) throws SQLException {
        String depId = getDepartmentId(employeeId); // Get the department ID of the employee being deleted

        String sql = "DELETE FROM employee WHERE employeeId=?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, employeeId);

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                // Decrease staff count in the department
                decreaseStaffCount(depId);
                return true; // Return true if deletion is successful
            }
            return false; // Return false if no rows were affected (deletion failed)
        }
    }



    private String getDepartmentId(String employeeId) throws SQLException {
        String sql = "SELECT depId FROM employee WHERE employeeId=?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, employeeId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("depId");
                }
                return null;
            }
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = pstm.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = new Employee(
                            resultSet.getString("employeeId"),
                            resultSet.getString("name"),
                            resultSet.getString("depId"),
                            resultSet.getString("position"),
                            resultSet.getString("duty")
                    );
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    public void loadEmployees(List<Employee> employeeList) throws SQLException {
        employeeList.clear();
        employeeList.addAll(getAllEmployees());
    }

    public static int getEmployeeCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        String sql = "SELECT COUNT(*) FROM employee";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Error getting employee count");
                }
            }
        }
    }

    private boolean increaseStaffCount(String depId) throws SQLException {
        String sql = "UPDATE department SET staffCount = staffCount + 1 WHERE depId = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    private boolean decreaseStaffCount(String depId) throws SQLException {
        String sql = "UPDATE department SET staffCount = staffCount - 1 WHERE depId = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, depId);
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
