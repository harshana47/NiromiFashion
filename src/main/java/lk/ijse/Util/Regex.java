package lk.ijse.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static boolean isTextFieldValid(TextField textField,String text){
        String field="";

        switch (textField){
            case ID:
                field = "^([A-Z][0-9]{3})$";
                break;
            case NAME:
                field = "^[A-z|\\\\s]{3,}$";
                break;
            case EMAIL:
                field = "^([A-z])([A-z0-9.]){1,}[@]([A-z0-9]){1,10}[.]([A-z]){2,5}$";
                break;
            case PHONE:
                field = ("[0-9]{10}");
                break;
            case COUNT:
                field = ("^[0-9]{1,}$");
                break;
            case POSITION:
                field = ("^[A-z|\\\\s]{3,}$");
                break;
            case DUTY:
                field = ("^[A-z|\\\\s]{3,}$");
                break;
            case AMOUNT:
                field = ("^[0-9]{1,}$");
                break;
            case QUANTITY:
                field = ("^[0-9]{1,}$");
                break;
            case TWOID:
                field = ("^([A-Z]{2}[0-9]{3})$");
                break;
            case THREEID:
                field= ("^([A-Z]{3}[0-9]{3})$");
                break;
            case DATE:
                field = ("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$");
        }
        Pattern pattern = Pattern.compile(field);

        if(text != null) {
            if (text.trim().isEmpty()) {
                return false;
            }
        }else {
            return false;
        }
        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }
    public static boolean setTextColor(TextField location, javafx.scene.control.TextField textField){
        if(Regex.isTextFieldValid(location,textField.getText())){
            textField.setStyle("-fx-text-fill: green;");
            textField.setStyle("-fx-text-fill: green;");

            return true;
        }else{
            textField.setStyle("-fx-text-fill: red;");
            textField.setStyle("-fx-text-fill: red;");
            return false;
        }
    }
}
