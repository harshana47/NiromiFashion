package lk.ijse.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.model.Customer;
import lk.ijse.repository.CustomerRepo;

import java.sql.SQLException;

public class CustomerFormController {
    @FXML
    private TextField txtCustomerId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<Customer> tblCustomers;

    @FXML
    private TableColumn<Customer, String> colCustomerId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colEmail;

    @FXML
    private TableColumn<Customer, String> colPhone;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private CustomerRepo customerRepo;

    public CustomerFormController() {
        try {
            // Initialize the CustomerRepo instance in the constructor
            customerRepo = new CustomerRepo();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error initializing CustomerRepo: " + e.getMessage());
        }
    }

    public void initialize() {
        colCustomerId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerId()));
        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmail()));
        colPhone.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPhone()));

        tblCustomers.setItems(customerList);
        loadCustomers();
    }

    private void loadCustomers() {
        try {
            customerList.clear();
            customerList.addAll(customerRepo.getAllCustomers());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customers: " + e.getMessage());
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String customerId = txtCustomerId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        Customer customer = new Customer(customerId, name, email, phone);

        try {
            boolean saved = customerRepo.save(customer);
            if (saved) {
                loadCustomers();
                clearFields();
                showAlert(Alert.AlertType.CONFIRMATION, "Customer saved successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save customer!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving customer: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            try {
                boolean deleted = customerRepo.delete(selectedCustomer.getCustomerId());
                if (deleted) {
                    customerList.remove(selectedCustomer);
                    showAlert(Alert.AlertType.CONFIRMATION, "Customer deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete customer!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error deleting customer: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a customer to delete!");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            String name = txtName.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            Customer updatedCustomer = new Customer(selectedCustomer.getCustomerId(), name, email, phone);

            try {
                boolean updated = customerRepo.update(updatedCustomer);
                if (updated) {
                    loadCustomers();
                    clearFields();
                    showAlert(Alert.AlertType.CONFIRMATION, "Customer updated successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to update customer!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error updating customer: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a customer to update!");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String customerId = txtCustomerId.getText();
        try {
            Customer customer = customerRepo.search(customerId);
            if (customer != null) {
                txtName.setText(customer.getName());
                txtEmail.setText(customer.getEmail());
                txtPhone.setText(customer.getPhone());
            } else {
                showAlert(Alert.AlertType.ERROR, "Customer not found!");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching customer: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtCustomerId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
