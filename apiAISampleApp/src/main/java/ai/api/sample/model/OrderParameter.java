package ai.api.sample.model;

/**
 * Created by anuj on 14/07/18.
 */
public class OrderParameter {
    private String foodType;
    private String restaurant;
    private String deliverAt;
    private String userName;
    private String note;
    private String item;

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setDeliverAt(String deliverAt) {
        this.deliverAt = deliverAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
