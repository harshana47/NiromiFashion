package lk.ijse.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.model.Payment;
import lk.ijse.repository.PaymentRepo;

import java.sql.SQLException;

public class PaymentFormController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<?, ?> colPayId;

    @FXML
    private TableColumn<?, ?> colPayMethod;

    @FXML
    private Label lblMethodId;

    @FXML
    private Label lblPaymentId;

    @FXML
    private TableView<Payment> tblPayment;

    @FXML
    private TextField txtMethod;
    @FXML
    private TextField txtPayId;

    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    private PaymentRepo paymentRepo;

    public PaymentFormController() {
        try {
            paymentRepo = new PaymentRepo();
        } catch (SQLException e) {
            // Handle the exception appropriately (e.g., log or show an error message)
            e.printStackTrace();
        }
    }

    @FXML
    public void btnAddOnAction(ActionEvent actionEvent) {
        String paymentId = txtPayId.getText();
        String method = txtMethod.getText();

        Payment payment = new Payment(paymentId, method);
        try {
            paymentRepo.addPayment(payment);
            loadPaymentDataIntoTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Payment selectedPayment = tblPayment.getSelectionModel().getSelectedItem();

        if (selectedPayment != null) {
            try {
                paymentRepo.deletePayment(selectedPayment);
                loadPaymentDataIntoTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Payment selectedPayment =tblPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            String newMethod = txtMethod.getText();
            selectedPayment.setMethod(newMethod);

            try {
                paymentRepo.updatePayment(selectedPayment);
                loadPaymentDataIntoTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String paymentId = txtPayId.getText();

        try {
            Payment payment = paymentRepo.searchPayment(paymentId);
            if (payment != null) {
                tblPayment.getItems().clear();
                tblPayment.getItems().add(payment);
            } else {
                showAlert(Alert.AlertType.ERROR, "Payment Search", "Payment with ID " + paymentId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        // Implement back button action as needed
    }
    private void loadProducts() {
        try {
            paymentList.clear(); // Clear existing items
            paymentList.addAll(paymentRepo.getAllPayment()); // Add products from repository
        } catch (SQLException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void initialize() {
        colPayId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colPayMethod.setCellValueFactory(new PropertyValueFactory<>("method"));

        tblPayment.setItems(paymentList);

        loadPaymentDataIntoTable(); // Call method to load payment data into table
    }

    private void loadPaymentDataIntoTable() {
        try {
            paymentList.clear(); // Clear existing items
            paymentList.addAll(paymentRepo.getAllPayment()); // Add payments from repository
        } catch (SQLException e) {
            System.out.println("Error loading payments: " + e.getMessage());
        }
    }

}

