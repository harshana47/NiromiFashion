package lk.ijse.model;

public class Promotion {
    private String promoId;
    private String promoName;
    private String discountPercentage;

    public Promotion() {
    }

    public Promotion(String promoId, String promoName, String discountPercentage) {
        this.promoId = promoId;
        this.promoName = promoName;
        this.discountPercentage = discountPercentage;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "promoId='" + promoId + '\'' +
                ", promoName='" + promoName + '\'' +
                ", discountPercentage='" + discountPercentage + '\'' +
                '}';
    }
}
