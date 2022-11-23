package com.example.server.diningapp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FoodItem {
    // The default information of a food item
    private @Id
    @GeneratedValue Long id;
    private String name;
    private String label;
    private String description;
    private String amount;
    private String type;
    private String diningHall;
    private String restaurant;
    private String otherInfo;

    //
    private int thumbUpCount;
    private int thumbDownCount;
    private int waitingLine;

    public FoodItem(String name,
                    String label,
                    String description,
                    String amount,
                    String type,
                    String diningHall,
                    String restaurant,
                    String otherInfo) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.diningHall = diningHall;
        this.restaurant = restaurant;
        this.otherInfo = otherInfo;
    }

    public FoodItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiningHall() {
        return diningHall;
    }

    public void setDiningHall(String diningHall) {
        this.diningHall = diningHall;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public int getThumbUpCount() {
        return thumbUpCount;
    }

    public void setThumbUpCount(int thumbUpCount) {
        this.thumbUpCount = thumbUpCount;
    }

    public int getThumbDownCount() {
        return thumbDownCount;
    }

    public void setThumbDownCount(int thumbDownCount) {
        this.thumbDownCount = thumbDownCount;
    }

    public int getWaitingLine() {
        return waitingLine;
    }

    public void setWaitingLine(int waitingLine) {
        this.waitingLine = waitingLine;
    }

    public static final class FoodItemBuilder {
        private String name;
        private String label;
        private String description;
        private String amount;
        private String type;
        private String diningHall;
        private String restaurant;
        private String otherInfo;
        private int thumbUpCount;
        private int thumbDownCount;
        private int waitingLine;

        public FoodItemBuilder() {
        }

        public static FoodItemBuilder aFoodItem() {
            return new FoodItemBuilder();
        }

        public FoodItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FoodItemBuilder label(String label) {
            this.label = label;
            return this;
        }

        public FoodItemBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FoodItemBuilder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public FoodItemBuilder type(String type) {
            this.type = type;
            return this;
        }

        public FoodItemBuilder diningHall(String diningHall) {
            this.diningHall = diningHall;
            return this;
        }

        public FoodItemBuilder restaurant(String restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public FoodItemBuilder otherInfo(String otherInfo) {
            this.otherInfo = otherInfo;
            return this;
        }

        public FoodItemBuilder thumbUpCount(int thumbUpCount) {
            this.thumbUpCount = thumbUpCount;
            return this;
        }

        public FoodItemBuilder thumbDownCount(int thumbDownCount) {
            this.thumbDownCount = thumbDownCount;
            return this;
        }

        public FoodItemBuilder otherInfo(int waitingTime) {
            this.waitingLine = waitingTime;
            return this;
        }

        public FoodItem build() {
            return new FoodItem(name, label, description, amount, type, diningHall, restaurant, otherInfo);
        }
    }
}
