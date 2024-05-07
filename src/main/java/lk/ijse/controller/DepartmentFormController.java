package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lk.ijse.Util.Regex;
import lk.ijse.model.Department;
import lk.ijse.repository.DepartmentRepo;
import lk.ijse.repository.EmployeeRepo;

import java.io.IOException;
import java.sql.SQLException;

public class DepartmentFormController {
    public JFXButton btnBack;
    private DepartmentRepo departmentRepo;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Department, String> colDepartmentId;

    @FXML
    private TableColumn<Department, String> colName;

    @FXML
    private TableColumn<Department, Integer> colStaffCount;

    @FXML
    private TableView<Department> tblDepartments;

    @FXML
    private TextField txtDeptId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtStaffCount;

    private ObservableList<Department> departmentList = FXCollections.observableArrayList();

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String depId = txtDeptId.getText();
        String name = txtName.getText();
        int staffCount = Integer.parseInt(txtStaffCount.getText());

        try {
            int employeeCount = EmployeeRepo.getEmployeeCount(); // Get the count of employees
            if (staffCount <= employeeCount) { // Check if staff count is less than or equal to employee count
                Department department = new Department(depId, name, staffCount);
                if (isValied()) {
                    boolean isAdded = departmentRepo.addDepartment(depId, name, staffCount);
                    if (isAdded) {
                        departmentList.add(department); // Add to ObservableList
                        new Alert(Alert.AlertType.CONFIRMATION, "Department added successfully!").show();
                        clearFields();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to add department!").show();
                    }
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Staff count cannot exceed the number of employees!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error adding department: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String depId = txtDeptId.getText();
        String name = txtName.getText();
        int staffCount = Integer.parseInt(txtStaffCount.getText());

        try {
            int employeeCount = EmployeeRepo.getEmployeeCount(); // Get the count of employees
            if (staffCount <= employeeCount) { // Check if staff count is less than or equal to employee count
                Department department = new Department(depId, name, staffCount);
                boolean isUpdated = departmentRepo.updateDepartment(department);
                if (isUpdated) {
                    initialize();
                    new Alert(Alert.AlertType.CONFIRMATION, "Department updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update department!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Staff count cannot exceed the number of employees!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error updating department: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException {
        Department selectedDepartment = tblDepartments.getSelectionModel().getSelectedItem();
        if (selectedDepartment != null) {
            boolean isDeleted = departmentRepo.deleteDepartment(selectedDepartment.getDepId());
            if (isDeleted) {
                departmentList.remove(selectedDepartment); // Remove from ObservableList
                new Alert(Alert.AlertType.CONFIRMATION, "Department deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete department!").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a department to delete!").show();
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent actionEvent) throws SQLException {
        String depId = txtDeptId.getText();
        Department department = departmentRepo.searchDepartment(depId);
        if (department != null) {
            txtName.setText(department.getName());
            txtStaffCount.setText(String.valueOf(department.getStaffCount()));
        } else {
            new Alert(Alert.AlertType.ERROR, "Department not found!").show();
            clearFields();
        }
    }

    private void clearFields() {
        txtDeptId.clear();
        txtName.clear();
        txtStaffCount.clear();
    }

    @FXML
    public void initialize() {
        try {
            departmentRepo = new DepartmentRepo(); // Initialize DepartmentRepo

            colDepartmentId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDepId()));
            colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
            colStaffCount.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getStaffCount()).asObject());

            tblDepartments.setItems(departmentList); // Set items to ObservableList

            departmentRepo.loadDepartments(departmentList); // Load departments into the ObservableList
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error initializing: " + e.getMessage()).show();
        }
    }

    @FXML
    public void onClose() throws SQLException {
        departmentRepo.closeConnection(); // Close database connection
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboardForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard Controller");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void txtDepartmentIDOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtDeptId);
    }

    public void txtDepartmentNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName);
    }

    public void txtDepartmentStaffOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.COUNT,txtStaffCount);
    }
    public boolean isValied(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtDeptId)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.COUNT,txtStaffCount)) return false;
        return true;
    }
}
