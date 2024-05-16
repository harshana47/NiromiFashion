package lk.ijse.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lk.ijse.Util.Regex;
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
        paymentRepo = new PaymentRepo();
    }

    @FXML
    public void btnAddOnAction(ActionEvent actionEvent) {
        String paymentId = txtPayId.getText();
        String method = txtMethod.getText();

        Payment payment = new Payment(paymentId, method);
        if (isValid()) {
            paymentRepo.addPayment(payment);
            loadPaymentDataIntoTable();
        }
    }


    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Payment selectedPayment = tblPayment.getSelectionModel().getSelectedItem();

        if (selectedPayment != null) {
            paymentRepo.deletePayment(selectedPayment);
            loadPaymentDataIntoTable();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Payment selectedPayment =tblPayment.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            String newMethod = txtMethod.getText();
            selectedPayment.setMethod(newMethod);

            paymentRepo.updatePayment(selectedPayment);
            loadPaymentDataIntoTable();
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String paymentId = txtPayId.getText();

        Payment payment = paymentRepo.searchPayment(paymentId);
        if (payment != null) {
            tblPayment.getItems().clear();
            tblPayment.getItems().add(payment);
        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Search", "Payment with ID " + paymentId + " not found.");
        }
    }

    private void loadProducts() {
        paymentList.clear();
        paymentList.addAll(paymentRepo.getAllPayment());
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

        loadPaymentDataIntoTable();
    }

    private void loadPaymentDataIntoTable() {
        paymentList.clear();
        paymentList.addAll(paymentRepo.getAllPayment());
    }

    public void txtPaymentMethodOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtMethod);
    }

    public void txtPaymentIDOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtPayId);
    }
    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtMethod)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtPayId)) return false;
        return true;
    }
}

