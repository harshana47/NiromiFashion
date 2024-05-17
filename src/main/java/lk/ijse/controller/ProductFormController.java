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
    private ComboBox<String> cmbSupplier;

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
    private TableColumn<Product, String> colPromotionId;

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
    private TextField txtPromotionId;

    private ProductRepo productRepo;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private SupplierProductDetailRepo supplierProductDetailRepo;


    public ProductFormController() {
        productRepo = new ProductRepo();
        supplierProductDetailRepo = new SupplierProductDetailRepo();
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        String name = txtName.getText();
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();
        String promoId = txtPromotionId.getText();
        String supplierName = cmbSupplier.getValue();

        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName);

        try {
            if(isValid()) {
                boolean isAddedProduct = productRepo.addProduct(product);
                if (isAddedProduct) {
                    tblProducts.getItems().add(product);

                    SupplierRepo supplierRepo = new SupplierRepo();

                    String supplierId = supplierRepo.getSupplierIdByName(supplierName);


                    SupplierProductDetail supplierProductDetail = SupplierProductDetail.builder()
                            .productId(productId)
                            .supplierId(supplierId)
                            .build();

                    //supplierProductDetails
                    boolean isAddedSupplierProductDetail = supplierProductDetailRepo.addSupplierProductDetail(supplierProductDetail);
                    if (isAddedSupplierProductDetail) {

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
        Product selectedProduct = tblProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            boolean isDeleted = productRepo.deleteProduct(selectedProduct.getProductId());
            if (isDeleted) {
                tblProducts.getItems().remove(selectedProduct);
            } else {
                System.out.println("Failed to delete product!");
            }
        } else {
            System.out.println("Please select a product to delete!");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        Product product = productRepo.searchProduct(productId);
        if (product != null) {
            txtName.setText(product.getName());
            txtExpireDate.setText(product.getExpireDate());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtQuantity.setText(String.valueOf(product.getQtyOnHand()));
            txtEmployeeId.setText(product.getEmployeeId());
            txtPromotionId.setText(product.getPromoId());
        } else {
            System.out.println("Product not found!");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String productId = txtProductId.getText();
        String name = txtName.getText();
        String expireDate = txtExpireDate.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQuantity.getText());
        String employeeId = txtEmployeeId.getText();
        String promoId = txtPromotionId.getText();
        String supplierName = cmbSupplier.getValue();

        Product product = new Product(productId, name, expireDate, price, qtyOnHand, employeeId, promoId, supplierName);

        boolean isUpdated = productRepo.updateProduct(product);
        if (isUpdated) {
            // Update product in TableView
            int selectedIndex = tblProducts.getSelectionModel().getSelectedIndex();
            tblProducts.getItems().set(selectedIndex, product);
            System.out.println("Product updated successfully!");
        } else {
            System.out.println("Failed to update product!");
        }
    }

    @FXML
    public void initialize() {
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

            loadProducts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProducts() throws SQLException {
        productList.clear();
        productList.addAll(productRepo.getAllProducts());
        tblProducts.setItems(productList);
    }


    private void clearFields() {
        txtProductId.clear();
        txtName.clear();
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

    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtProductId)) return false;;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.DATE,txtExpireDate)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.AMOUNT,txtPrice)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.QUANTITY,txtQuantity)) return false;
        return true;
    }
}
