package lk.ijse.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Util.Regex;
import lk.ijse.db.DbConnection;
import lk.ijse.model.*;
import lk.ijse.model.tm.CartTm;
import lk.ijse.repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderFormController {

    private final ProductRepo productRepo = new ProductRepo();
    private final PromotionRepo promotionRepo = new PromotionRepo();
    private final OrderRepo orderRepo = new OrderRepo();
    private final CustomerRepo customerRepo = new CustomerRepo();
    private final List<OrderProductDetail> productDetails = new ArrayList<>();
    public Label lblTotal;
    public Button btnPrintBill;
    public ComboBox txtPaymentId;
    public Button btnAddCus;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblCurrentDate;
    @FXML
    private Button btnCheckout;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnAdd;
    @FXML
    private TableColumn<CartTm, String> colProductId;
    @FXML
    private TableColumn<CartTm, Integer> colQuantity;
    @FXML
    private TableColumn<CartTm, Double> colPrice;
    @FXML
    private TableColumn<CartTm, String> colDiscount;
    @FXML
    private TextField txtOrderId;
    @FXML
    private TextField txtProductId;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private ComboBox txtPromoId;
    @FXML
    private Label lblCustomer;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblExpireDiscountStatus;
    @FXML
    private TableView<CartTm> tblOrders;
    @FXML
    public AnchorPane node;

    private final ObservableList<CartTm> obList = FXCollections.observableArrayList();
    private PaymentRepo paymentRepo = new PaymentRepo();

    public OrderFormController() throws SQLException {
    }

    @FXML
    private void initialize() {
        generateOrderId();
        lblCurrentDate.setText(LocalDate.now().toString());

        txtQuantity.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE) {
                handleQuantityChanged();
            } else if (event.getCode() == KeyCode.ENTER) {
                txtPromoId.requestFocus();
            }
        });

        txtCustomerId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleCustomerIdEntered();
            }
        });

        // Initialize TableView columns
        colProductId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProductId()));
        colQuantity.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getQty()).asObject());

        ObservableList<String> paymentMethods = FXCollections.observableArrayList(paymentRepo.getAllPaymentMethods());
        txtPaymentId.setItems(paymentMethods);
        try {
            ObservableList<String> promotions = FXCollections.observableArrayList(promotionRepo.getAllPromoNames());
            txtPromoId.setItems(promotions);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        colPrice.setCellValueFactory(cellData -> {
            CartTm cartItem = cellData.getValue();
            return new ReadOnlyDoubleWrapper(calculateGeneratedPrice(cartItem.getProductId(), cartItem.getQty())).asObject();
        });

        colDiscount.setCellValueFactory(cellData -> {
            CartTm cartItem = cellData.getValue();
            String productId = cartItem.getProductId();
            String promoId = (String) txtPromoId.getValue();
            try {
                String promo = promotionRepo.findPromotionByName(promoId);
                Promotion promotion = promotionRepo.findPromotionById(promo);
                if (promotion != null) {
                    return new ReadOnlyStringWrapper(promotion.getDiscountPercentage() + "%");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ReadOnlyStringWrapper("");
        });
    }

    private double calculateGeneratedPrice(String productId, int quantity) {
        Product product = productRepo.findProductById(productId);
        if (product != null) {
            BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

            if ("given".equals(lblExpireDiscountStatus.getText())) {
                price = price.multiply(BigDecimal.valueOf(0.5));
            }

            return price.doubleValue();
        }
        return 0.0;
    }


    private void generateOrderId() {
        try {
            String nextOrderId = orderRepo.getNextOrderId();
            txtOrderId.setText(nextOrderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleQuantityChanged() {
        calculatePrice();
        calculateExpireStatus();

    }

    @FXML
    private void calculatePrice() {
        try {
            String productId = txtProductId.getText();
            String quantityText = txtQuantity.getText();

            if (productId.isEmpty() || quantityText.isEmpty()) {
                lblPrice.setText("Please enter Product ID and Quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            Product product = productRepo.findProductById(productId);

            if (product != null) {
                BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

                if ("given".equals(lblExpireDiscountStatus.getText())) {
                    price = price.multiply(BigDecimal.valueOf(0.5));
                }

                lblPrice.setText(price.toString());
            } else {
                lblPrice.setText("Invalid Product ID");
            }

        } catch (NumberFormatException e) {
            lblPrice.setText("Error calculating price");
            e.printStackTrace();
        }
    }

    private void calculateExpireStatus() {
        try {
            String productId = txtProductId.getText();
            Product product = productRepo.findProductById(productId);
            if (product != null) {
                LocalDate currentDate = LocalDate.now();
                LocalDate expirationDate = productRepo.getProductExpirationDate(productId);
                if (expirationDate != null && expirationDate.isBefore(currentDate.plusMonths(5))) {
                    lblExpireDiscountStatus.setText("given");
                } else {
                    lblExpireDiscountStatus.setText("not");
                }
            } else {
                lblExpireDiscountStatus.setText("error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCustomerIdEntered() {
        try {
            String phone = txtCustomerId.getText();
            if (!phone.isEmpty()) {
                Customer customer = customerRepo.findCustomerByPhone(phone);
                if (customer != null) {
                    lblCustomer.setText(customer.getName());
                    txtCustomerId.setText(customer.getCustomerId());
                } else {
                    lblCustomer.setText("Customer not found");
                }
            } else {
                lblCustomer.setText("Please enter Customer ID");
            }
        } catch (SQLException e) {
            lblCustomer.setText("Error fetching customer details");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAddOnAction() {
        try {
            String orderId = txtOrderId.getText();
            String productId = txtProductId.getText();
            String quantityText = txtQuantity.getText();
            String promoId = (String) txtPromoId.getValue();

            if (orderId.isEmpty() || productId.isEmpty() || quantityText.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            Product product = productRepo.findProductById(productId);
            if (product == null) {
                new Alert(Alert.AlertType.ERROR, "Invalid Product ID").show();
                return;
            }

            BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

            if ("given".equals(lblExpireDiscountStatus.getText())) {
                price = price.multiply(BigDecimal.valueOf(0.5)); // 50% discount
            }

            if (!promoId.isEmpty()) {
                if (promoId != null) {
                    promoId = promotionRepo.findPromotionByName(promoId);
                }
                Promotion promotion = promotionRepo.findPromotionById(promoId);
                if (promotion != null) {
                    BigDecimal discountPercentage = BigDecimal.valueOf(Double.parseDouble(promotion.getDiscountPercentage()));
                    BigDecimal discountAmount = price.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
                    price = price.subtract(discountAmount);
                }
            }

            CartTm cartItem = new CartTm(productId, quantity, price.doubleValue());
            if (isValid()) {
                obList.add(cartItem);
                tblOrders.setItems(obList);

                updateTotal();

                txtProductId.clear();
                txtQuantity.clear();
                lblPrice.setText("");
                txtProductId.requestFocus();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid input").show();
            e.printStackTrace();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error adding item: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    private void updateTotal() {
        double total = obList.stream().mapToDouble(item -> item.getPrice()).sum();
        lblTotal.setText(String.format("%.2f", total));
    }

    @FXML
    private void btnRemoveOnAction(ActionEvent actionEvent) {
        CartTm selectedItem = tblOrders.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            obList.remove(selectedItem);
            updateTotal();
        } else {
            new Alert(Alert.AlertType.ERROR, "Please select an item to remove").show();
        }
    }

    @FXML
    private void btnClearOnAction(ActionEvent actionEvent) {
        txtPromoId.setValue(null);
        lblCustomer.setText("");
        lblExpireDiscountStatus.setText("");
        obList.clear();
        tblOrders.getItems().clear();
        obList.clear();
        tblOrders.getItems().clear();
    }

    @FXML
    private void btnCheckoutOnAction(ActionEvent actionEvent) throws SQLException {
        String orderId = txtOrderId.getText();
        String customerId = txtCustomerId.getText();
        String total = lblTotal.getText();
        String promoName = (String) txtPromoId.getValue();
        String promoId = null;
        if (promoName != null) {
            promoId = promotionRepo.findPromotionByName(promoName);
        }
        String expireStatus = lblExpireDiscountStatus.getText();
        String paymentMethod = (String) txtPaymentId.getValue();
        try {
            String paymentId = paymentRepo.getPaymentIdByMethod(paymentMethod);

            Order order = new Order();
            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setPaymentId(paymentId);
            order.setPromoId(promoId);
            order.setExpireDiscountStatus(expireStatus);
            order.setTotalAmount(Double.valueOf(total));

            LocalDate orderDate = LocalDate.now();
            order.setOrderDate(orderDate);

            List<OrderProductDetail> list = obList.stream().map(product -> {
                return OrderProductDetail.builder()
                        .orderId(orderId)
                        .productId(product.getProductId())
                        .qty(product.getQty())
                        .total(order.getTotalAmount())
                        .orderDate(orderDate)
                        .build();
            }).toList();
            order.setOrderProductDetailList(list);

            boolean isOrderSaved = OrderRepo.saveOrder(order);
            new Alert(Alert.AlertType.INFORMATION,
                    isOrderSaved ? "Order saved successfully"
                            : "Ooops something went wrong").show();
            generateOrderId();

            obList.clear();
            tblOrders.getItems().clear();

            txtPromoId.setValue(null);
            txtCustomerId.clear();
            lblPrice.setText("");
            lblTotal.setText("");
            lblExpireDiscountStatus.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error getting payment ID: " + e.getMessage()).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saving order: " + e.getMessage()).show();
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectionForm.fxml"));
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
    @FXML
    private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                switch (textField.getId()) {
                    case "txtProductId":
                        txtQuantity.requestFocus();
                        break;
                    case "txtQuantity":
                        break;
                    case "txtPromoId":
                        txtCustomerId.requestFocus();
                        break;
                    case "txtCustomerId":
                        break;
                }
            }
        }
    }

    public void txtOderQuantityOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.COUNT, txtQuantity);
    }

    public boolean isValid() {
        if (!Regex.setTextColor(lk.ijse.Util.TextField.COUNT, txtQuantity)) ;
        return true;
    }

    public void btnPrintBillOnAction(ActionEvent actionEvent) throws JRException, SQLException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Niromi.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);


        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DbConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint, false);
    }

    public void btnAddCusOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/customerForm.fxml"));
        this.node.getChildren().clear();
        this.node.getChildren().add(root);
    }
}
