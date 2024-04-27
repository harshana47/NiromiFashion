package lk.ijse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @FXML
    public void initialize() {
        try {
            customerRepo = new CustomerRepo();
            tblCustomers.setItems(customerList);
            loadCustomers(); // Load customers into the table view
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

        // Initialize table columns
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadCustomers() {
        try {
            customerList.clear();
            customerRepo.loadCustomers(customerList);
            tblCustomers.setItems(customerList);
            tblCustomers.refresh();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String customerId = txtCustomerId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        Customer customer = new Customer(customerId, name, email, phone); // Passing null for address

        try {
            boolean saved = customerRepo.save(customer);
            if (saved) {
                loadCustomers(); // Refresh data in TableView
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
                    customerList.remove(selectedCustomer); // Update TableView by removing the deleted customer
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
            String customerId = selectedCustomer.getCustomerId();
            String name = txtName.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            // Fetch the existing customer's address
            String address = selectedCustomer.getPhone();

            Customer updatedCustomer = new Customer(customerId, name, phone, email);

            try {
                boolean updated = customerRepo.update(updatedCustomer);
                if (updated) {
                    loadCustomers(); // Refresh data in TableView
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
            Customer customer = customerRepo.findCustomerById(customerId);
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
