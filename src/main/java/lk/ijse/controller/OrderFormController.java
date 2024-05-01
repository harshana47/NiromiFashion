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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderFormController {

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

    private final List<OrderItem> orderItems = new ArrayList<>();

    public OrderFormController() throws SQLException {
    }

    @FXML
    private void initialize() {
        txtQuantity.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE) {
                handleQuantityChanged();
            }
        });
    }

    private void handleQuantityChanged() {
        calculatePrice();
        calculatePromoId();
    }

    @FXML
    private void calculatePrice() {
        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            String productId = txtProductId.getText();

            Product product = productRepo.findProductById(productId);
            if (product != null) {
                double price = product.getPrice() * quantity;
                lblPrice.setText(String.valueOf(price));
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
            OrderItem item = new OrderItem(txtProductId.getText(), Integer.parseInt(txtQuantity.getText()), Double.parseDouble(lblPrice.getText()));
            orderItems.add(item);

            Order order = new Order(orderId, LocalDate.now(), 0.0, customerId, paymentId, promoId, expireStatus);
            order.setOrderItems(orderItems);

            orderRepo.saveOrder(order);
            orderItems.clear(); // Clear order items after saving
            lblPrice.setText(String.valueOf(order.getTotalAmount()));

            // Clear input fields after successful save
            txtOrderId.clear();
            txtProductId.clear();
            txtQuantity.clear();
            txtCustomerId.clear();
            txtPaymentId.clear();
            txtPromoId.clear();

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
        // Implement checkout logic here
    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        // Implement back button logic here
    }

    // Other methods for UI interactions
}
