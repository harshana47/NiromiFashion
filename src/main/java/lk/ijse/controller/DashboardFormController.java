package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.db.DbConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardFormController {
    @FXML
    public JFXButton btnDepartment;
    public JFXButton btnExit;
    public JFXButton btnProduct;
    public Label lblSoldCount;
    public Label lblSoonExpire;
    public Button btnExpireSoon;
    public JFXButton btnRegister;
    public Label lblDate;

    @FXML
    private AnchorPane rootNode;
    @FXML
    private AnchorPane node;

    private final Connection connection;

    public DashboardFormController() {
        try {
            this.connection = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing database connection");
        }
    }

    @FXML
    void initialize() {
        lblDate.setText(LocalDate.now().toString());
        updateSoldCount();
        updateSoonToExpire();
    }
    private void checkExpiringProducts() {
        try {
            Connection connection = DbConnection.getInstance().getConnection();
            LocalDate currentDate = LocalDate.now();
            LocalDate oneMonthLater = currentDate.plusMonths(1);

            String sql = "SELECT productId FROM product WHERE expireDate >= ? AND expireDate <= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, currentDate);
            preparedStatement.setObject(2, oneMonthLater);

            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder expiringProducts = new StringBuilder();

            while (resultSet.next()) {
                String productName = resultSet.getString("productId");
                expiringProducts.append(productName).append("\n");
            }

            if (expiringProducts.length() > 0) {
                showAlert("Expiring Products", "The following products are expiring soon:\n" + expiringProducts.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling
        }
    }

   private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void btnCustomerOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/customerForm.fxml"));
        //checkExpiringProducts();

        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    @FXML
    void btnDepartmentOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/departmentForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();

    }

    @FXML
    void btnEmployeeOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/employeeForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();
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
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnPaymentOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/paymentForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();

    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/orderForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();

    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/supplierForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();
    }

    @FXML
    void btnProductOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/productForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        checkExpiringProducts();
    }

    public void btnPromotionOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/promotionForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();
    }

    private void updateSoldCount() {
        try {
            String sql = "SELECT SUM(quantity) AS totalQuantity FROM orderProductDetails WHERE date = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int totalQuantity = rs.getInt("totalQuantity");
                lblSoldCount.setText(String.valueOf(totalQuantity) + " Items Sold");
            } else {
                lblSoldCount.setText("Oh! 0 items sold today");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblSoldCount.setText("Error fetching sold count");
        }
    }

    private void updateSoonToExpire() {
        try {
            String sql = "SELECT COUNT(*) AS soonExpireCount FROM product WHERE expireDate BETWEEN ? AND ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pst.setDate(2, java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int soonExpireCount = rs.getInt("soonExpireCount");
                lblSoonExpire.setText(String.valueOf(soonExpireCount) + " Items Soon to Expire");
            } else {
                lblSoonExpire.setText("No items are soon to expire");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblSoonExpire.setText("Error fetching soon to expire count");
        }
    }

    public void btnExpireSoonOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/expireSoonForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
        //checkExpiringProducts();
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/registrationForm.fxml"));

        Scene scene = new Scene(rootNode);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setTitle("Registration Form");

        stage.show();
    }

    public void btnSalesOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/salesForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }

    public void btnDashboardOnAction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/dashboardForm.fxml"));
            Stage stage = (Stage) rootNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Form");
            stage.centerOnScreen();
            stage.show();
            checkExpiringProducts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
