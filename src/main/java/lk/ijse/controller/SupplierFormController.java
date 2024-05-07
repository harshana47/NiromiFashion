package lk.ijse.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lk.ijse.Util.Regex;
import lk.ijse.model.Supplier;
import lk.ijse.repository.SupplierRepo;

import java.io.IOException;
import java.sql.SQLException;

public class SupplierFormController {

    public Button btnBack;
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSearch;

    @FXML
    private TableColumn<Supplier, String> colSupplierId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    @FXML
    private TableColumn<Supplier, String> colContact;

    @FXML
    private TableColumn<Supplier, String> colEmail;

    @FXML
    private TableView<Supplier> tblSuppliers;

    @FXML
    private TextField txtSupplierId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtContact;

    @FXML
    private TextField txtEmail;

    private SupplierRepo supplierRepo;
    private ObservableList<Supplier> supplierList;

    public SupplierFormController() {
        try {
            supplierRepo = new SupplierRepo(); // Initialize the repository
            supplierList = FXCollections.observableArrayList(); // Initialize the ObservableList
        } catch (SQLException e) {
            System.out.println("Error initializing supplier repository: " + e.getMessage());
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String supplierId = txtSupplierId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String contact = txtContact.getText();
        String email = txtEmail.getText();

        Supplier supplier = new Supplier(supplierId, name, address, contact, email);

        try {
            boolean isAdded = supplierRepo.addSupplier(supplier);
            if (isAdded) {
                tblSuppliers.getItems().add(supplier);
                clearFields();
            } else {
                System.out.println("Failed to add supplier to the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding supplier: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Supplier selectedSupplier = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            try {
                boolean isDeleted = supplierRepo.deleteSupplier(selectedSupplier.getSupplierId());
                if (isDeleted) {
                    tblSuppliers.getItems().remove(selectedSupplier);
                } else {
                    System.out.println("Failed to delete supplier from the database.");
                }
            } catch (SQLException e) {
                System.out.println("Error deleting supplier: " + e.getMessage());
            }
        } else {
            System.out.println("Please select a supplier to delete.");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Supplier selectedSupplier = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            String name = txtName.getText();
            String address = txtAddress.getText();
            String contact = txtContact.getText();
            String email = txtEmail.getText();

            selectedSupplier.setName(name);
            selectedSupplier.setAddress(address);
            selectedSupplier.setContact(contact);
            selectedSupplier.setEmail(email);

            try {
                boolean isUpdated = supplierRepo.updateSupplier(selectedSupplier);
                if (isUpdated) {
                    tblSuppliers.refresh(); // Refresh the table view
                    clearFields();
                } else {
                    System.out.println("Failed to update supplier in the database.");
                }
            } catch (SQLException e) {
                System.out.println("Error updating supplier: " + e.getMessage());
            }
        } else {
            System.out.println("Please select a supplier to update.");
        }
    }
    @FXML
    public void btnSearchOnAction(ActionEvent actionEvent) {
        String id = txtSupplierId.getText();
        try {
            Supplier supplier = supplierRepo.searchSupplier(id);
            if (supplier != null) {
                txtName.setText(supplier.getName());
                txtAddress.setText(supplier.getAddress());
                txtContact.setText(supplier.getContact());
                txtEmail.setText(supplier.getEmail());
                new Alert(Alert.AlertType.INFORMATION, "Supplier found!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Supplier not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error searching for supplier: " + e.getMessage()).show();
        }
    }



    private void clearFields() {
        txtSupplierId.clear();
        txtName.clear();
        txtAddress.clear();
        txtContact.clear();
        txtEmail.clear();
    }

    @FXML
    public void initialize() {
        try {
            colSupplierId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSupplierId()));
            colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
            colAddress.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAddress()));
            colContact.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getContact()));
            colEmail.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmail()));

            tblSuppliers.setItems(supplierList); // Set items to TableView
            loadSuppliers(); // Load suppliers into the ObservableList
        } catch (SQLException e) {
            System.out.println("Error initializing: " + e.getMessage());
        }
    }

    private void loadSuppliers() throws SQLException {
        supplierList.clear(); // Clear existing items
        supplierList.addAll(supplierRepo.getAllSuppliers()); // Load suppliers from the repository
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

    public void txtSupplierIdOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.ID,txtSupplierId);
    }

    public void txtNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName);
    }

    public void txtAddressOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtAddress);
    }

    public void txtEmailOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.EMAIL,txtEmail);
    }

    public void txtContactOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.PHONE,txtContact);
    }
}
