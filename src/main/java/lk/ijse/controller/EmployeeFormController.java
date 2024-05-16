package lk.ijse.controller;

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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Util.Regex;
import lk.ijse.model.Employee;
import lk.ijse.repository.EmployeeRepo;

import java.io.IOException;
import java.sql.SQLException;

public class EmployeeFormController {
    public TextField txtEmployeeId;
    public TextField txtName;
    public Button btnDelete;
    public Button btnSearch;
    public Button btnUpdate;
    public Button btnSave;
    public Button btnBack;
    public TextField txtPosition;
    public TextField txtDuty;
    public TextField txtEmail;

    @FXML
    private TextField txtDeptId;

    private EmployeeRepo employeeRepo;

    @FXML
    private TableView<Employee> tblEmployees;

    @FXML
    private TableColumn<Employee, String> colEmail;

    @FXML
    private TableColumn<Employee, String> colEmployeeId;

    @FXML
    private TableColumn<Employee, String> colName;

    @FXML
    private TableColumn<Employee, String> colDeptId;

    @FXML
    private TableColumn<Employee, String> colPosition;

    @FXML
    private TableColumn<Employee, String> colDuty;

    private AnchorPane node;


    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    public EmployeeFormController() {
        employeeRepo = new EmployeeRepo();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtEmployeeId.getText();
        String name = txtName.getText();
        String depId = txtDeptId.getText();
        String position = txtPosition.getText();
        String duty = txtDuty.getText();
        String email = txtEmail.getText();

        if (isValid()) {
            Employee employee = new Employee(id, name, depId, position, duty, email);

            boolean isSaved = employeeRepo.save(employee);
            if (isSaved) {
                employeeList.add(employee);
                new Alert(Alert.AlertType.CONFIRMATION, "Employee added successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add employee!!").show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtEmployeeId.getText();
        String name = txtName.getText();
        String depId = txtDeptId.getText();
        String position = txtPosition.getText();
        String duty = txtDuty.getText();
        String email = txtEmail.getText();

        Employee employee = new Employee(id, name, depId, position, duty, email);

        boolean isUpdated = employeeRepo.update(employee);
        if (isUpdated) {
            initialize();
            new Alert(Alert.AlertType.CONFIRMATION, "Employee updated successfully!").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to update employee!").show();
        }
    }

    @FXML
    public void btnSearchOnAction(ActionEvent actionEvent) {
        String id = txtEmployeeId.getText();
        Employee employee = employeeRepo.search(id);
        if (employee != null) {
            txtName.setText(employee.getName());
            txtDeptId.setText(employee.getDepId());
            txtPosition.setText(employee.getPosition());
            txtDuty.setText(employee.getDuty());
            txtEmail.setText(employee.getEmail());
            new Alert(Alert.AlertType.INFORMATION, "Employee found!").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Employee not found!").show();
        }
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Employee selectedEmployee = tblEmployees.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            boolean isDeleted = employeeRepo.delete(selectedEmployee.getEmployeeId());
            if (isDeleted) {
                employeeList.remove(selectedEmployee);
                new Alert(Alert.AlertType.CONFIRMATION, "Employee deleted successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete employee!").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please select an employee to delete!").show();
        }
    }

    @FXML
    public void initialize() {
        try {
            employeeRepo = new EmployeeRepo();

            colEmployeeId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmployeeId()));
            colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
            colDeptId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDepId()));
            colPosition.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPosition()));
            colDuty.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDuty()));
            colEmail.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmail()));
            tblEmployees.setItems(employeeList) ;

            employeeRepo.loadEmployees(employeeList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error initializing: " + e.getMessage()).show();
        }
    }

    public void txtEmployeeIDOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.ID,txtEmployeeId);
    }

    public void txtEmployeeNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName);
    }

    public void txtEmployeePositionOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtPosition);
    }

    public void txtEmployeeDutyOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtDuty);
    }

    public void txtDepartmentIDOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtDeptId);
    }
    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtEmployeeId));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtPosition));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtDuty));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtDeptId));
        return true;
    }
}
