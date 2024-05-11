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
    public Button net;
    public Label lblnetTotal;
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

        try {
            ObservableList<String> paymentMethods = FXCollections.observableArrayList(paymentRepo.getAllPaymentMethods());
            txtPaymentId.setItems(paymentMethods);
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

    private double calculateGeneratedPrice(String productId, int quantity) {
        try {
            Product product = productRepo.findProductById(productId);
            if (product != null) {
                BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

                // Apply discount only if lblExpireDiscountStatus is "given"
                if ("given".equals(lblExpireDiscountStatus.getText())) {
                    price = price.multiply(BigDecimal.valueOf(0.5)); // 50% discount
                }

                return price.doubleValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default value if calculation fails
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

            if (productId.isEmpty() || quantityText.isEmpty()) {
                lblPrice.setText("Please enter Product ID and Quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            Product product = productRepo.findProductById(productId);

            if (product != null) {
                BigDecimal price = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

                // Apply discount only if lblExpireDiscountStatus is "given"
                if ("given".equals(lblExpireDiscountStatus.getText())) {
                    price = price.multiply(BigDecimal.valueOf(0.5)); // 50% discount
                }

                lblPrice.setText(price.toString()); // Set the calculated price to lblPrice
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
            String promoId = txtPromoId.getText();

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

            // Apply discount if lblExpireDiscountStatus is "given"
            if ("given".equals(lblExpireDiscountStatus.getText())) {
                price = price.multiply(BigDecimal.valueOf(0.5)); // 50% discount
            }

            // Apply promotion discount if a valid promotion is found
            if (!promoId.isEmpty()) {
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

                updateTotal(); // Update total price after adding item

                txtProductId.clear();
                txtQuantity.clear();
                lblPrice.setText(""); // Clear lblPrice after adding item
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
        lblTotal.setText(String.format("%.2f", total)); // Set the total without promotion discount initially
    }

    @FXML
    private void btnCalculateTotalOnAction(ActionEvent actionEvent) {
       /* try {
            String promoId = txtPromoId.getText();
            if (!promoId.isEmpty()) {
                Promotion promotion = promotionRepo.findPromotionById(promoId);
                if (promotion != null) {
                    double discountPercentage = Double.parseDouble(promotion.getDiscountPercentage());
                    double total = Double.parseDouble(lblTotal.getText());
                    double netTotal = total * (discountPercentage / 100);
                    lblnetTotal.setText(String.format("%.2f", netTotal)); // Set the net total after applying promotion discount
                }
            } else {
                lblnetTotal.setText(""); // Clear net total if no promotion ID is entered
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error calculating net total: " + e.getMessage()).show();
        }*/
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
        txtPromoId.clear();
        lblCustomer.setText("");
        lblExpireDiscountStatus.setText("");
        obList.clear();
        tblOrders.getItems().clear();
    }

    @FXML
    private void btnCheckoutOnAction(ActionEvent actionEvent) {
        String orderId = txtOrderId.getText();
        String customerId = txtCustomerId.getText();
        String total = lblTotal.getText();
        String promoId = txtPromoId.getText();
        String expireStatus = lblExpireDiscountStatus.getText();
        String paymentMethod = (String) txtPaymentId.getValue(); // Get the selected payment method from ComboBox

        try {
            String paymentId = paymentRepo.getPaymentIdByMethod(paymentMethod); // Get the payment ID based on the selected method

            Order order = new Order();
            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setPaymentId(paymentId); // Set the retrieved payment ID in the order
            order.setPromoId(promoId);
            order.setExpireDiscountStatus(expireStatus);
            order.setTotalAmount(Double.valueOf(total));

            LocalDate orderDate = LocalDate.now(); // You can change this based on your requirements
            order.setOrderDate(orderDate);

            List<OrderProductDetail> list = obList.stream().map(product -> {
                return OrderProductDetail.builder()
                        .orderId(orderId)
                        .productId(product.getProductId())
                        .qty(product.getQty())
                        .total(order.getTotalAmount())
                        .orderDate(orderDate) // Set the order date for each OrderProductDetail
                        .build();
            }).toList();
            order.setOrderProductDetailList(list);

            boolean isOrderSaved = OrderRepo.saveOrder(order);
            new Alert(Alert.AlertType.INFORMATION,
                    isOrderSaved ? "Order saved successfully"
                            : "Ooops something went wrong").show();
            generateOrderId();

            txtPromoId.clear();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboardForm.fxml"));
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


    //public void txtPaymentIDOnKeyReleased(KeyEvent keyEvent) {
    //Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtPaymentId);
    //}

    //public void txtPromotionPromoIDOnKeyReleased(KeyEvent keyEvent) {
    //   Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtPromoId);
    //}

    //public void txtCustomerIDOnKeyReleased(KeyEvent keyEvent) {
    //   Regex.setTextColor(lk.ijse.Util.TextField.ID,txtCustomerId);
    //}

    public void txtOderQuantityOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.COUNT, txtQuantity);
    }

    //public void txtProductIDOnKeyReleased(KeyEvent keyEvent) {
    //Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtProductId);
    //}

    //public void txtOrderIDOnKeyReleased(KeyEvent keyEvent) {
    //Regex.setTextColor(lk.ijse.Util.TextField.ID,txtOrderId);
    //}
    public boolean isValid() {
        //if (!Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtPaymentId));
        //if (!Regex.setTextColor(lk.ijse.Util.TextField.THREEID,txtPromoId));
        //if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtCustomerId));
        if (!Regex.setTextColor(lk.ijse.Util.TextField.COUNT, txtQuantity)) ;
        //if (!Regex.setTextColor(lk.ijse.Util.TextField.TWOID,txtProductId));
        //if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtOrderId));
        return true;
    }

    public void btnPrintBillOnAction(ActionEvent actionEvent) throws JRException, SQLException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Niromi.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        // JRDesignQuery designQuery = new JRDesignQuery();
        // designQuery.setText("SELECT  opd.itemPrice, o.orderId, o.orderDate, p.name\n" +
        //         "FROM orders o\n" +
        //         "JOIN orderProductDetails opd ON o.orderId = opd.orderId\n" +
        //         "JOIN product p ON opd.productId = p.productId;");
        // jasperDesign.setQuery(designQuery);

        //JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DbConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint, false);
    }

}
