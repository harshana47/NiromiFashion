package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import lk.ijse.model.*;
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

    public Button btnBack;
    public Label lblCurrentDate;
    public Button btnCheckout;
    public Button btnClear;
    public Button btnRemove;
    public Button btnAdd;

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
    private TableView<Order> tblOrders;

    private final ProductRepo productRepo = new ProductRepo();
    private final PromotionRepo promotionRepo = new PromotionRepo();
    private final OrderRepo orderRepo = new OrderRepo();
    private final CustomerRepo customerRepo = new CustomerRepo();

    private final List<OrderProductDetail> productDetails = new ArrayList<>();

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
            String customerId = txtCustomerId.getText();
            String paymentId = txtPaymentId.getText();
            String promoId = txtPromoId.getText();
            String expireStatus = lblExpireDiscountStatus.getText();

            // Validate input fields
            if (orderId.isEmpty() || customerId.isEmpty() || paymentId.isEmpty()) {
                lblPrice.setText("Please fill all fields");
                return;
            }

            Customer customer = customerRepo.findCustomerById(customerId);
            if (customer != null) {
                lblCustomer.setText(customer.getName());
            } else {
                lblCustomer.setText("Customer not found");
                return;
            }

            // Create and set order items based on user input
            OrderProductDetail item = new OrderProductDetail(txtOrderId.getText(), txtProductId.getText(), txtQuantity.getText());
            productDetails.add(item); // Add item to productDetails list

            Order order = new Order(orderId, LocalDate.now(), 0.0, customerId, paymentId, promoId, expireStatus);
            order.setOrderItems(productDetails); // Set productDetails list in order

            orderRepo.saveOrder(order);
            productDetails.clear(); // Clear productDetails list after saving
            lblPrice.setText(String.valueOf(order.getTotalAmount()));

            // Clear input fields after successful save
            txtOrderId.clear();
            txtProductId.clear();
            txtQuantity.clear();
            txtCustomerId.clear();
            txtPaymentId.clear();
            txtPromoId.clear();
            lblCustomer.setText("");

        } catch (NumberFormatException | SQLException e) {
            lblPrice.setText("Invalid input or error saving order");
            e.printStackTrace(); // Consider logging the error
        }
    }

    @FXML
    private void btnRemoveOnAction(ActionEvent actionEvent) {
        // Implement removal logic here
    }

    @FXML
    private void btnClearOnAction(ActionEvent actionEvent) {
        // Implement clear logic here
    }

    @FXML
    private void btnCheckoutOnAction(ActionEvent actionEvent) {
        try {
            String nextOrderId = orderRepo.getNextOrderId(); // Get the next order ID from the database
            txtOrderId.setText(nextOrderId); // Set the next order ID in the txtOrderId TextField
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        // Implement back button logic here
    }

    // Other methods for UI interactions
}
