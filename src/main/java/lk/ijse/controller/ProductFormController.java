package lk.ijse.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import lk.ijse.Util.Regex;
import lk.ijse.model.Product;
import lk.ijse.model.SupplierProductDetail;
import lk.ijse.repository.ProductRepo;
import lk.ijse.repository.SupplierProductDetailRepo;
import lk.ijse.repository.SupplierRepo;

import java.sql.SQLException;
import java.util.List;

public class ProductFormController {

    @FXML
    private ComboBox<String> cmbSupplier; // Specify the type of ComboBox items

    @FXML
    private TableColumn<Product, String> colSupplier;

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
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, String> colExpireDate;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQtyOnHand;

    @FXML
    private TableColumn<Product, String> colEmployeeId;

    @FXML
    private TableColumn<Product, String> colPromotionId; // Added column for promotionId

    @FXML
    private TableView<Product> tblProducts;

    @FXML
    private TextField txtProductId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtExpireDate;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtEmployeeId;

    @FXML
    private TextField txtPromotionId; // Added TextField for promotionId

    private ProductRepo productRepo;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private SupplierProductDetailRepo supplierProductDetailRepo;


    public ProductFormController() {
        try {
            productRepo = new ProductRepo();
            supplierProductDetailRepo = new SupplierProductDetailRepo(); // Initialize the repository
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        // Get input values from text fields and ComboBox
        String productId = txtProductId.getText();
        String name = txtName.getText();
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();
        String promoId = txtPromotionId.getText();
        String supplierName = cmbSupplier.getValue();

        // Create Product object
        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName);

        try {
            if(isValid()) {
                boolean isAddedProduct = productRepo.addProduct(product);
                if (isAddedProduct) {
                    // Add product to TableView
                    tblProducts.getItems().add(product);

                    // Create an instance of SupplierRepo to access non-static methods
                    SupplierRepo supplierRepo = new SupplierRepo();

                    // Get supplier ID from supplier name
                    String supplierId = supplierRepo.getSupplierIdByName(supplierName);

                    // Close the connection after getting the supplier ID

                    // Create SupplierProductDetail object
                    SupplierProductDetail supplierProductDetail = SupplierProductDetail.builder()
                            .productId(productId)
                            .supplierId(supplierId)
                            .build();

                    // Add supplier product detail to supplierProductDetails table
                    boolean isAddedSupplierProductDetail = supplierProductDetailRepo.addSupplierProductDetail(supplierProductDetail);
                    if (isAddedSupplierProductDetail) {
                        // Update the supplier name in the colSupplier column
                        colSupplier.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(supplierName));

                        clearFields();
                    } else {
                        System.out.println("Failed to add supplier product detail!");
                    }
                } else {
                    System.out.println("Failed to add product!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        // Get selected product from TableView
        Product selectedProduct = tblProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                // Delete product from repository
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
        // Get productId from text field
        String productId = txtProductId.getText();
        try {
            // Search for product in repository
            Product product = productRepo.searchProduct(productId);
            if (product != null) {
                // Set product details in text fields
                txtName.setText(product.getName());
                txtExpireDate.setText(product.getExpireDate());
                txtPrice.setText(String.valueOf(product.getPrice()));
                txtQuantity.setText(String.valueOf(product.getQtyOnHand()));
                txtEmployeeId.setText(product.getEmployeeId());
                txtPromotionId.setText(product.getPromoId()); // Set promoId in text field
            } else {
                System.out.println("Product not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error searching for product: " + e.getMessage());
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        // Get input values from text fields
        String productId = txtProductId.getText();
        String name = txtName.getText();
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();
        String promoId = txtPromotionId.getText(); // Get promoId from text field
        String supplierName = cmbSupplier.getValue(); // Use getValue directly for ComboBox<String>

        // Create Product object
        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName);

        try {
            // Update product in repository
            boolean isUpdated = productRepo.updateProduct(product);
            if (isUpdated) {
                // Update product in TableView
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
        // Set cell value factories for TableView columns
        colProductId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProductId()));
        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        colExpireDate.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getExpireDate()));
        colPrice.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(cellData.getValue().getPrice()).asObject());
        colQtyOnHand.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getQtyOnHand()).asObject());
        colEmployeeId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmployeeId()));
        colPromotionId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPromoId()));
        colSupplier.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSupplierName()));

        try {
            List<String> supplierNames = SupplierRepo.getAllSupplierNames();
            cmbSupplier.setItems(FXCollections.observableArrayList(supplierNames));

            loadProducts(); // Load products into the TableView after setting ComboBox items
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProducts() throws SQLException {
        productList.clear(); // Clear existing items
        productList.addAll(productRepo.getAllProducts()); // Add products from repository
        tblProducts.setItems(productList); // Set items to TableView
    }



    private void clearFields() {
        txtProductId.clear();
        txtName.clear(); // Clear name text field
        txtExpireDate.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtEmployeeId.clear();
        txtPromotionId.clear();
    }

    public void txtProductIdOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtProductId);
    }

    public void txtNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName);
    }

    public void txtExpireDateOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.DATE,txtExpireDate);
    }

    public void txtPriceOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.AMOUNT,txtPrice);
    }

    public void txtQuantityOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.QUANTITY,txtQuantity);
    }

   // public void txtEmployeeIdOnKeyReleased(KeyEvent keyEvent) {
   // }

    //public void txtPromotionIdOnKeyReleased(KeyEvent keyEvent) {
    //}
    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtProductId)) return false;;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtName)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.DATE,txtExpireDate)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.AMOUNT,txtPrice)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.QUANTITY,txtQuantity)) return false;
        return true;
    }
}
