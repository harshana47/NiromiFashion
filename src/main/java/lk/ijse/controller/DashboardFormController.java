package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.cert.PolicyNode;

public class DashboardFormController {
    @FXML
    public JFXButton btnDepartment;
    public JFXButton btnExit;
    public JFXButton btnProduct;
    public Label lblSoldCount;
    public ProgressIndicator pgsProgress;

    @FXML
    private AnchorPane rootNode;
    @FXML
    private AnchorPane node;

    @FXML
    void btnCustomerOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/customerForm.fxml"));

        this.node.getChildren().clear();
        this.node.getChildren().add(root);
       /* try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/customerForm.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) rootNode.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Customer Form");
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();}*/

    }

    @FXML
    void btnDepartmentOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/departmentForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnEmployeeOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/employeeForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnExitOnAction(ActionEvent event) throws IOException {

         try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginForm.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) rootNode.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Customer Form");
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();
         }
    }

    @FXML
    void btnPaymentOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/paymentForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/orderForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnReportOnAction(ActionEvent event) {

    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/supplierForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnProductOnAction(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/productForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    public void btnPromotionOnAction(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/promotionForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }
}