package lk.ijse.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.model.Product;
import lk.ijse.repository.ProductRepo;

import java.sql.SQLException;

public class ProductFormController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Product, String> colProductId;

    @FXML
    private TableColumn<Product, String> colName; // Updated column name

    @FXML
    private TableColumn<Product, String> colExpireDate;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQtyOnHand;

    @FXML
    private TableColumn<Product, String> colEmployeeId;

    @FXML
    private TableView<Product> tblProducts;

    @FXML
    private TextField txtProductId;

    @FXML
    private TextField txtName; // Updated text field for name

    @FXML
    private TextField txtExpireDate;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtEmployeeId;

    private ProductRepo productRepo;
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    public ProductFormController() {
        try {
            productRepo = new ProductRepo(); // Initialize the repository
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        String name = txtName.getText(); // Get name from text field
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();

        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId);

        try {
            boolean isAdded = productRepo.addProduct(product);
            if (isAdded) {
                tblProducts.getItems().add(product);
                clearFields();
            } else {
                System.out.println("Failed to add product!");
            }
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Product selectedProduct = tblProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                boolean isDeleted = productRepo.deleteProduct(selectedProduct.getProductId());
                if (isDeleted) {
                    tblProducts.getItems().remove(selectedProduct);
                } else {
                    System.out.println("Failed to delete product!");
                }
            } catch (SQLException e) {
                System.out.println("Error deleting product: " + e.getMessage());
            }
        } else {
            System.out.println("Please select a product to delete!");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        try {
            Product product = productRepo.searchProduct(productId);
            if (product != null) {
                txtName.setText(product.getName()); // Set name in text field
                txtExpireDate.setText(product.getExpireDate());
                txtPrice.setText(String.valueOf(product.getPrice()));
                txtQuantity.setText(String.valueOf(product.getQtyOnHand()));
                txtEmployeeId.setText(product.getEmployeeId());
            } else {
                System.out.println("Product not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error searching for product: " + e.getMessage());
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        String name = txtName.getText(); // Get name from text field
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();

        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId);

        try {
            boolean isUpdated = productRepo.updateProduct(product);
            if (isUpdated) {
                int selectedIndex = tblProducts.getSelectionModel().getSelectedIndex();
                tblProducts.getItems().set(selectedIndex, product);
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Failed to update product!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        colProductId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProductId()));
        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName())); // Updated column value factory
        colExpireDate.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getExpireDate()));
        colPrice.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(cellData.getValue().getPrice()).asObject());
        colQtyOnHand.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getQtyOnHand()).asObject());
        colEmployeeId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmployeeId()));

        tblProducts.setItems(productList); // Set items to TableView

        loadProducts(); // Load products into the TableView
    }

    private void loadProducts() {
        try {
            productList.clear(); // Clear existing items
            productList.addAll(productRepo.getAllProducts()); // Add products from repository
        } catch (SQLException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtProductId.clear();
        txtName.clear(); // Clear name text field
        txtExpireDate.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtEmployeeId.clear();
    }
}
