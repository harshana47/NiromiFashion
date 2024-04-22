package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class OrderFormController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCheckout;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnRemove;

    @FXML
    private Label lblCurrentDate;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblPrice;

    @FXML
    private TableView<?> tblOrders;

    @FXML
    private TextField txtCustomerId;

    @FXML
    private TextField txtOrderId;

    @FXML
    private TextField txtProductId;

    @FXML
    private TextField txtPromoId;

    @FXML
    private TextField txtQuantity;

    @FXML
    void btnAddOnAction(ActionEvent event) {

    }

    @FXML
    void btnCheckoutOnAction(ActionEvent event) {

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {

    }

    @FXML
    void calculatePrice(ActionEvent event) {

    }

}
