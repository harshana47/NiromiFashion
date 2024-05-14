package lk.ijse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.model.OrderProductDetail;
import lk.ijse.repository.OrderDetailRepo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SalesFormController {
    public TableColumn<OrderProductDetail, String> colOrderId;
    public TableColumn<OrderProductDetail, String> colProductId;
    public TableColumn<OrderProductDetail, Integer> colQuantity;
    public TableColumn<OrderProductDetail, Double> colPrice;
    public TableColumn<OrderProductDetail, LocalDate> colDate;
    public Button btnBack;
    public Button btnSearch;
    public TableView<OrderProductDetail> tblSales;

    private OrderDetailRepo orderProductDetailRepo;

    @FXML
    public void initialize() throws SQLException {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        orderProductDetailRepo = new OrderDetailRepo();

        try {
            loadOrderProductDetails();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's logic
        }
    }

    private void loadOrderProductDetails() throws SQLException {
        List<OrderProductDetail> orderProductDetails = orderProductDetailRepo.getAllOrderProductDetails();
        ObservableList<OrderProductDetail> data = FXCollections.observableArrayList(orderProductDetails);
        tblSales.setItems(data);
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        // Handle button action
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        // Handle button action
    }
}
