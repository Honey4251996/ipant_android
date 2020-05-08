package com.ipant.activities.home.fragment.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class TransactionBean implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<Transaction> data = null;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("errorcode")
    @Expose
    private Integer errorcode;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Transaction> getData() {
        return data;
    }

    public void setData(List<Transaction> data) {
        this.data = data;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Transaction implements Serializable {
        @SerializedName("Id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("order_number")
        @Expose
        private String orderNumber;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("charge")
        @Expose
        private String charge;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("transaction_status")
        @Expose
        private String transactionStatus;
        @SerializedName("trx_type")
        @Expose
        private String trxType;
        @SerializedName("tran_type_id")
        @Expose
        private String tranTypeId;
        @SerializedName("msg")
        @Expose
        private String msg;
        @SerializedName("time")
        @Expose
        private String time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCharge() {
            return charge;
        }

        public void setCharge(String charge) {
            this.charge = charge;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }

        public void setTransactionStatus(String transactionStatus) {
            this.transactionStatus = transactionStatus;
        }

        public String getTrxType() {
            return trxType;
        }

        public void setTrxType(String trxType) {
            this.trxType = trxType;
        }

        public String getTranTypeId() {
            return tranTypeId;
        }

        public void setTranTypeId(String tranTypeId) {
            this.tranTypeId = tranTypeId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }


        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }
}
