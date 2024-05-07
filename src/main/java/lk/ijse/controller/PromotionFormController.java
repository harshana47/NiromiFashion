package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lk.ijse.Util.Regex;
import lk.ijse.model.Promotion;
import lk.ijse.repository.PromotionRepo;

import java.io.IOException;
import java.sql.SQLException;

public class PromotionFormController {
    public TextField txtPromotionId;
    public TextField txtPromotionName;
    public Button btnBack;
    @FXML
    private TableView<Promotion> tblPromotions;

    @FXML
    private TableColumn<Promotion, String> colPromotionId;

    @FXML
    private TableColumn<Promotion, String> colPromotionName;

    @FXML
    private TableColumn<Promotion, String> colDiscountPercentage;

    @FXML
    private TextField txtDiscountPercentage;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    private ObservableList<Promotion> promotionList = FXCollections.observableArrayList();
    private PromotionRepo promotionRepo;

    @FXML
    void initialize() {
        try {
            colPromotionId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPromoId()));
            colPromotionName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPromoName()));
            colDiscountPercentage.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDiscountPercentage()));

            promotionRepo = new PromotionRepo(); // Initialize promotionRepo
            tblPromotions.setItems(promotionList);
            loadPromotions();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error initializing: " + e.getMessage());
        }
    }


    private void loadPromotions() throws SQLException {
        promotionList.clear();
        promotionList.addAll(promotionRepo.getAllPromotions());
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String promoId = txtPromotionId.getText();
        String promoName = txtPromotionName.getText();
        String discountPercentage = txtDiscountPercentage.getText();

        Promotion promotion = new Promotion(promoId, promoName, discountPercentage);

        try {
            if (isValid()) {
                boolean saved = promotionRepo.save(promotion);
                if (saved) {
                    loadPromotions();
                    clearFields();
                    showAlert(Alert.AlertType.CONFIRMATION, "Promotion saved successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to save promotion!");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving promotion: " + e.getMessage());
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String promoId = txtPromotionId.getText();
        String promoName = txtPromotionName.getText();
        String discountPercentage = txtDiscountPercentage.getText();

        Promotion promotion = new Promotion(promoId, promoName, discountPercentage);

        try {
            boolean updated = promotionRepo.update(promotion);
            if (updated) {
                loadPromotions();
                clearFields();
                showAlert(Alert.AlertType.CONFIRMATION, "Promotion updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update promotion!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating promotion: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Promotion selectedPromotion = tblPromotions.getSelectionModel().getSelectedItem();
        if (selectedPromotion != null) {
            try {
                boolean deleted = promotionRepo.delete(selectedPromotion.getPromoId());
                if (deleted) {
                    promotionList.remove(selectedPromotion);
                    showAlert(Alert.AlertType.CONFIRMATION, "Promotion deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete promotion!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error deleting promotion: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a promotion to delete!");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String promoId = txtPromotionId.getText();
        try {
            Promotion promotion = promotionRepo.search(promoId);
            if (promotion != null) {
                txtPromotionName.setText(promotion.getPromoName());
                txtDiscountPercentage.setText(promotion.getDiscountPercentage());
            } else {
                showAlert(Alert.AlertType.ERROR, "Promotion not found!");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching promotion: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtPromotionId.clear();
        txtPromotionName.clear();
        txtDiscountPercentage.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
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

    public void txtPromotionIdOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.ID,txtPromotionId);
    }

    public void txtPromotionNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtPromotionName);
    }

    public void txtDiscountPercentageOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Util.TextField.COUNT,txtDiscountPercentage);
    }
    public boolean isValid(){
        if (!Regex.setTextColor(lk.ijse.Util.TextField.ID,txtPromotionId)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.NAME,txtPromotionName)) return false;
        if (!Regex.setTextColor(lk.ijse.Util.TextField.COUNT,txtDiscountPercentage)) return false;
        return true;
    }
}
