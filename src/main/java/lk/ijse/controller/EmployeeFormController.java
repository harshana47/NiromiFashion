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
    @FXML
    private TextField txtDeptId; // Ensure this matches the fx:id in your FXML file

    private EmployeeRepo employeeRepo;

    @FXML
    private TableView<Employee> tblEmployees;

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

    private AnchorPane rootNode;


    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    public EmployeeFormController() {
        try {
            employeeRepo = new EmployeeRepo(); // Initialize the repository
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtEmployeeId.getText();
        String name = txtName.getText();
        String depId = txtDeptId.getText(); // Access txtDepId here
        String position = txtPosition.getText();
        String duty = txtDuty.getText();

        if (isValid()) {
            Employee employee = new Employee(id, name, depId, position, duty);

            try {
                boolean isSaved = employeeRepo.save(employee);
                if (isSaved) {
                    employeeList.add(employee);
                    new Alert(Alert.AlertType.CONFIRMATION, "Employee added successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add employee!!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error saving employee: " + e.getMessage()).show();
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

        Employee employee = new Employee(id, name, depId, position, duty);

        try {
            boolean isUpdated = employeeRepo.update(employee);
            if (isUpdated) {
                initialize();
                new Alert(Alert.AlertType.CONFIRMATION, "Employee updated successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update employee!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error updating employee: " + e.getMessage()).show();
        }
    }

    @FXML
    public void btnSearchOnAction(ActionEvent actionEvent) {
        String id = txtEmployeeId.getText();
        try {
            Employee employee = employeeRepo.search(id);
            if (employee != null) {
                txtName.setText(employee.getName());
                txtDeptId.setText(employee.getDepId());
                txtPosition.setText(employee.getPosition());
                txtDuty.setText(employee.getDuty());
                new Alert(Alert.AlertType.INFORMATION, "Employee found!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Employee not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error searching for employee: " + e.getMessage()).show();
        }
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Employee selectedEmployee = tblEmployees.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                boolean isDeleted = employeeRepo.delete(selectedEmployee.getEmployeeId());
                if (isDeleted) {
                    employeeList.remove(selectedEmployee);
                    new Alert(Alert.AlertType.CONFIRMATION, "Employee deleted successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete employee!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error deleting employee: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please select an employee to delete!").show();
        }
    }

    @FXML
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

    @FXML
    public void initialize() {
        try {
            employeeRepo = new EmployeeRepo(); // Initialize EmployeeRepo

            colEmployeeId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmployeeId()));
            colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
            colDeptId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDepId()));
            colPosition.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPosition()));
            colDuty.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDuty()));

            tblEmployees.setItems(employeeList); // Set items to ObservableList

            employeeRepo.loadEmployees(employeeList); // Load employees into the ObservableList
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error initializing: " + e.getMessage()).show();
        }
    }

    @FXML
    public void onClose() {
        try {
            employeeRepo.closeConnection(); // Close database connection
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error closing connection: " + e.getMessage()).show();
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
        Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtDeptId);
    }
    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtEmployeeId));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtPosition));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtDuty));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtDeptId));
        return true;
    }
}
