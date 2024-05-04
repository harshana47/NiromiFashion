package lk.ijse.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import lk.ijse.model.*;
import lk.ijse.model.tm.CartTm;
import lk.ijse.repository.CustomerRepo;
import lk.ijse.repository.OrderRepo;
import lk.ijse.repository.ProductRepo;
import lk.ijse.repository.PromotionRepo;

import java.math.BigDecimal;
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
    private TextField txtPaymentId;
    @FXML
    private TextField txtPromoId;
    @FXML
    private Label lblCustomer;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblExpireDiscountStatus;
    @FXML
    private TableView<CartTm> tblOrders;
    private final ObservableList<CartTm> obList = FXCollections.observableArrayList();

    public OrderFormController() throws SQLException {
    }

    @FXML
    private void initialize() {
        generateOrderId();

        txtQuantity.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE) {
                handleQuantityChanged();
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
        colPrice.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(cellData.getValue().getPrice()).asObject());
        colProductId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProductId()));
        colQuantity.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getQty()).asObject());
        colPrice.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(cellData.getValue().getPrice()).asObject());
        colDiscount.setCellValueFactory(cellData -> {
            CartTm cartItem = cellData.getValue();
            String productId = cartItem.getProductId();
            String promoId = txtPromoId.getText(); // Get the promo ID from the input field
            try {
                Promotion promotion = promotionRepo.findPromotionById(promoId);
                if (promotion != null) {
                    return new ReadOnlyStringWrapper(promotion.getDiscountPercentage() + "%");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ReadOnlyStringWrapper(""); // Return empty string if no promotion found
        });
    }

    private void generateOrderId() {
        try {
            String nextOrderId = orderRepo.getNextOrderId(); // Get the next order ID from the database
            txtOrderId.setText(nextOrderId); // Set the next order ID in the txtOrderId TextField
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleQuantityChanged() {
        calculatePrice();
        calculatePromoId();
    }

    @FXML
    private void calculatePrice() {
        try {
            String productId = txtProductId.getText();
            String quantityText = txtQuantity.getText();
            String promoId = txtPromoId.getText();

            if (productId.isEmpty() || quantityText.isEmpty()) {
                lblPrice.setText("Please enter Product ID and Quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            Product product = productRepo.findProductById(productId);
            Promotion promotion = promotionRepo.findPromotionById(promoId);

            if (product != null) {
                BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

                // Apply discount if a valid promotion is found
                if (promotion != null) {
                    BigDecimal discountPercentage = BigDecimal.valueOf(Double.parseDouble(promotion.getDiscountPercentage()));
                    BigDecimal discountAmount = price.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
                    BigDecimal discountedPrice = price.subtract(discountAmount);
                    lblPrice.setText(discountedPrice.toString());
                } else {
                    lblPrice.setText(price.toString()); // No discount applied
                }
            } else {
                lblPrice.setText("Invalid Product ID");
            }
        } catch (NumberFormatException | SQLException e) {
            lblPrice.setText("Error calculating price");
            e.printStackTrace();
        }
    }

    private void calculatePromoId() {
        try {
            String productId = txtProductId.getText();
            Product product = productRepo.findProductById(productId);
            if (product != null) {
                LocalDate currentDate = LocalDate.now();
                LocalDate expirationDate = productRepo.getProductExpirationDate(productId);
                if (expirationDate != null && expirationDate.isBefore(currentDate.plusMonths(5))) {
                    lblExpireDiscountStatus.setText("given");
                    txtPromoId.setText(null);
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
            String customerId = txtCustomerId.getText();
            if (!customerId.isEmpty()) {
                Customer customer = customerRepo.findCustomerById(customerId);
                if (customer != null) {
                    lblCustomer.setText(customer.getName());
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

            // Validate input fields
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
            CartTm cartItem = new CartTm(productId, quantity, price.doubleValue());
            obList.add(cartItem);
            tblOrders.setItems(obList);

            updateTotal();

            txtProductId.clear();
            txtQuantity.clear();
            lblPrice.setText("");


        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid input").show();
            e.printStackTrace();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error adding item: " + e.getMessage()).show();
            e.printStackTrace();
        }
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
        txtOrderId.clear();
        txtCustomerId.clear();
        txtPaymentId.clear();
        txtPromoId.clear();
        lblExpireDiscountStatus.setText("");
        obList.clear();
    }

    @FXML
    private void btnCheckoutOnAction(ActionEvent actionEvent) {
        String orderId = txtOrderId.getText();
        String customerId = txtCustomerId.getText();
        String paymentId = txtPaymentId.getText();
        String total = lblTotal.getText();
        String promoId = txtPromoId.getText();
        String expireStatus = lblExpireDiscountStatus.getText();


        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setPaymentId(paymentId);
        order.setPromoId(promoId);
        order.setExpireDiscountStatus(expireStatus);
        order.setTotalAmount(Double.valueOf(total));

        List<OrderProductDetail> list = obList.stream().map(product -> {
            return OrderProductDetail.builder()
                    .orderId(orderId)
                    .productId(product.getProductId())
                    .qty(product.getQty())
                    .build();
        }).toList();
        order.setOrderProductDetailList(list);

        try{
            boolean isOrderSaved = OrderRepo.saveOrder(order);
            new Alert(Alert.AlertType.INFORMATION,
                    isOrderSaved ? "Order saved successfully"
                    : "Ooops something went wrong").show();
        }catch(Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saving order: " + e.getMessage()).show();
        }

    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        // Implement back button logic here
    }
    @FXML
    private void updateTotal() {
        double total = obList.stream().mapToDouble(item -> item.getPrice()).sum();
        lblTotal.setText(String.valueOf(total));
    }

}
