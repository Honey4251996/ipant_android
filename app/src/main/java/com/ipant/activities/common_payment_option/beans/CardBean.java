package com.ipant.activities.common_payment_option.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardBean {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<CardData> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errorcode")
    @Expose
    private Integer errorcode;



    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<CardData> getData() {
        return data;
    }

    public void setData(List<CardData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }


    public class CardData {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("card_number")
        @Expose
        private String cardNumber;
        @SerializedName("expiry_month")
        @Expose
        private String expiryMonth;
        @SerializedName("expiry_year")
        @Expose
        private String expiryYear;
        @SerializedName("bank_image")
        @Expose
        private String bankImage;
        @SerializedName("bank_name")
        @Expose
        private String bankName;
        @SerializedName("card_type")
        @Expose
        private String cardType;

        private boolean cardSelected=false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpiryMonth() {
            return expiryMonth;
        }

        public void setExpiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
        }

        public String getExpiryYear() {
            return expiryYear;
        }

        public void setExpiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
        }

        public String getBankImage() {
            return bankImage;
        }

        public void setBankImage(String bankImage) {
            this.bankImage = bankImage;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public boolean isCardSelected() {
            return cardSelected;
        }

        public void setCardSelected(boolean cardSelected) {
            this.cardSelected = cardSelected;
        }

    }


}



