package lk.ijse.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.db.DbConnection;
import lk.ijse.model.OrderProductDetail;
import lk.ijse.model.Payment;
import lk.ijse.repository.OrderDetailRepo;
import lk.ijse.repository.SalesRepo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesFormController {
    public TableColumn<OrderProductDetail, String> colOrderId;
    public TableColumn<OrderProductDetail, String> colProductId;
    public TableColumn<OrderProductDetail, Integer> colQuantity;
    public TableColumn<OrderProductDetail, Double> colPrice;
    public TableColumn<OrderProductDetail, LocalDate> colDate;
    public Button btnBack;
    public Button btnSearch;
    public TableView<OrderProductDetail> tblSales;
    public Button btnReport;
    public TextField txtDate;
    public TextField txtId;
    public Button btnRefresh;

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
            e.printStackTrace();
        }
    }

    private void loadOrderProductDetails() throws SQLException {
        List<OrderProductDetail> orderProductDetails = orderProductDetailRepo.getAllOrderProductDetails();
        ObservableList<OrderProductDetail> data = FXCollections.observableArrayList(orderProductDetails);
        tblSales.setItems(data);
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String orderId = txtId.getText();

        OrderProductDetail sales = SalesRepo.searchSale(orderId);
        if (sales != null) {
            tblSales.getItems().clear();
            tblSales.getItems().add(sales);
        } else {
            new Alert(Alert.AlertType.ERROR,"Cant find sales id").show();
        }
    }

    public void btnReportOnAction(ActionEvent actionEvent) {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Sales.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("p_txtDate", txtDate.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, DbConnection.getInstance().getConnection());

            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnRefreashOnAction(ActionEvent actionEvent) throws SQLException {
        loadOrderProductDetails();
    }
}
