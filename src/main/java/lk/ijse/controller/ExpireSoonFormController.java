package lk.ijse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Product;
import lk.ijse.repository.EmployeeRepo;
import lk.ijse.repository.ProductRepo;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ExpireSoonFormController {

    public Button btnMail;
    public AnchorPane rootNode;
    public AnchorPane node;

    @FXML
    private TableColumn<Product, String> colEmployeeId;

    @FXML
    private TableColumn<Product, LocalDate> colExpireDate;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, String> colProductId;

    @FXML
    private TableColumn<Product, String> colPromotionId;

    @FXML
    private TableColumn<Product, Integer> colQtyOnHand;

    @FXML
    private TableColumn<Product, String> colSupplier;

    @FXML
    private TableView<Product> tblExpireProducts;

    private final Connection connection;
    private ProductRepo productRepo;
    private EmployeeRepo employeeRepo;

    public ExpireSoonFormController() throws SQLException {
        this.employeeRepo = new EmployeeRepo();
        this.connection = DbConnection.getInstance().getConnection();
        this.productRepo = new ProductRepo();
    }

    @FXML
    void initialize() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colExpireDate.setCellValueFactory(new PropertyValueFactory<>("expireDate"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colPromotionId.setCellValueFactory(new PropertyValueFactory<>("promoId"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        loadExpireProducts();
    }


    private void loadExpireProducts() {
        try {
            String sql = "SELECT * FROM product WHERE expireDate BETWEEN ? AND ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pst.setDate(2, java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));

            ResultSet rs = pst.executeQuery();
            ObservableList<Product> productList = FXCollections.observableArrayList();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("productId"));
                product.setName(rs.getString("name"));
                product.setExpireDate(rs.getString("expireDate")); // Check column name
                product.setPrice(rs.getDouble("price"));
                product.setQtyOnHand(rs.getInt("qtyOnHand"));
                product.setEmployeeId(rs.getString("employeeId"));
                product.setPromoId(rs.getString("promoId"));
                product.setSupplierName(rs.getString("supplierName"));

                productList.add(product);
            }

            tblExpireProducts.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Product selectedProduct = tblExpireProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Delete product from repository
            boolean isDeleted = productRepo.deleteProduct(selectedProduct.getProductId());
            if (isDeleted) {
                tblExpireProducts.getItems().remove(selectedProduct);
            } else {
                System.out.println("Failed to delete product!");
            }
        } else {
            System.out.println("Please select a product to delete!");
        }
    }

    public void btnMailOnAction(ActionEvent actionEvent) {
        sendEmail();
    }

    public void sendEmail() {
        try {
            List<String> employeeEmails = employeeRepo.getAllEmployeeEmails();
            List<Product> expiringProducts = productRepo.getExpiringProducts(LocalDate.now().plusMonths(2));

            StringBuilder emailBody = new StringBuilder("Hello,\n\nThe following products are expiring within 2 months:\n");
            for (Product product : expiringProducts) {
                emailBody.append("Product ID: ").append(product.getProductId())
                        .append(", Name: ").append(product.getName()).append("\n");
            }

            String subject = "Expiring Products Notification";
            String encodedEmailBody = URLEncoder.encode(emailBody.toString(), StandardCharsets.UTF_8);
            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8);

            StringBuilder emailAddresses = new StringBuilder();
            for (String employeeEmail : employeeEmails) {
                emailAddresses.append(employeeEmail).append(",");
            }
            emailAddresses.deleteCharAt(emailAddresses.length() - 1);

            String url = "https://mail.google.com/mail/?view=cm&fs=1&to=" + emailAddresses.toString() + "&body=" + encodedEmailBody + "&su=" + encodedSubject;

            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            System.out.println("An error occurred: " + e.getLocalizedMessage());
        }
    }

}




