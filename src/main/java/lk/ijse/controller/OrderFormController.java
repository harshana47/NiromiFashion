package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    public Button btnAdd;
    public Button btnRemove;
    public Button btnClear;
    public Button btnCheckout;
    public TextField txtPaymentId;
    public Label lblCurrentDate;
    public Label lblExpireDiscountStatus;
    public Button btnBack;

    @FXML
    private TextField txtOrderId;
    @FXML
    private TextField txtProductId;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtPromoId;
    @FXML
    private Label lblCustomer;
    @FXML
    private Label lblPrice;
    @FXML
    private TableView<Order> tblOrders;

    private ProductRepo productRepo;
    private PromotionRepo promotionRepo;
    private OrderRepo orderRepo;
    private CustomerRepo customerRepo;

    private List<OrderItem> orderItems; // Declare orderItems

    public OrderFormController() throws SQLException {
        productRepo = new ProductRepo();
        promotionRepo = new PromotionRepo();
        orderRepo = new OrderRepo();
        customerRepo = new CustomerRepo();
        orderItems = new ArrayList<>(); // Initialize orderItems
    }

    @FXML
    private void initialize() {
        txtQuantity.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleQuantityEnterPressed();
            }
        });

        txtCustomerId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleCustomerEnterPressed();
            }
        });
    }

    private void handleQuantityEnterPressed() {
        try {
            calculatePrice();
            calculatePromoId();
        } catch (NumberFormatException e) {
            lblPrice.setText("Invalid quantity");
            lblCustomer.setText("");
            e.printStackTrace();
        }
    }

    private void handleCustomerEnterPressed() {
        try {
            String customerId = txtCustomerId.getText();
            Customer customer = customerRepo.findCustomerById(customerId);
            if (customer != null) {
                lblCustomer.setText(customer.getName());
            } else {
                lblCustomer.setText("Customer not found");
            }
        } catch (SQLException e) {
            lblCustomer.setText("Error fetching customer");
            e.printStackTrace();
        }
    }

    @FXML
    private void calculatePrice() {
        try {
            String productId = txtProductId.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());
            Product product = productRepo.findProductById(productId);
            if (product != null) {
                double price = product.getPrice() * quantity;

                String promoId = txtPromoId.getText();
                Promotion promotion = promotionRepo.findPromotionById(promoId);
                double discount = (promotion != null) ? Double.parseDouble(promotion.getDiscountPercentage()) : 0;
                double discountedPrice = price - (price * discount / 100);

                lblPrice.setText(String.valueOf(discountedPrice));
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
                return; // Exit the method if any field is empty
            }

            // Automatically fill customer name based on customer ID
            Customer customer = customerRepo.findCustomerById(customerId);
            if (customer != null) {
                lblCustomer.setText(customer.getName());
            } else {
                lblCustomer.setText("Customer not found");
                return; // Exit the method if customer not found
            }

            // Create the order instance
            Order order = new Order(orderId, LocalDate.now(), 0.0, customerId, paymentId, promoId, expireStatus);

            // Add order items (products) to the order
            for (OrderItem item : orderItems) {
                double itemPrice = calculateItemPrice(item.getProductId(), item.getQuantity());
                order.addItem(item.getProductId(), item.getQuantity(), itemPrice);
                order.setTotalAmount(order.getTotalAmount() + itemPrice); // Update total amount
            }

            // Save the order to the database
            orderRepo.saveOrder(order);

            // Clear order items after saving
            orderItems.clear();

            // Update the total amount label
            lblPrice.setText(String.valueOf(order.getTotalAmount()));

        } catch (NumberFormatException | SQLException e) {
            lblPrice.setText("Invalid quantity or error saving order");
            e.printStackTrace();
        }
    }

    private double calculateItemPrice(String productId, int quantity) throws SQLException {
        Product product = productRepo.findProductById(productId);
        if (product != null) {
            double price = product.getPrice();
            return price * quantity;
        } else {
            throw new SQLException("Invalid Product ID");
        }
    }

    public void btnRemoveOnAction(ActionEvent actionEvent) {
    }

    public void btnClearOnAction(ActionEvent actionEvent) {
    }

    public void btnCheckoutOnAction(ActionEvent actionEvent) {
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
    }

    // Other methods for UI interactions
}
