package com.ipant.activities.common_payment_option.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetails implements Parcelable {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;


    public TransactionDetails() {

    }

    public TransactionDetails(Parcel parcel) {

        fullName = parcel.readString();
        transactionId = parcel.readString();
        transactionDate = parcel.readString();
        amount = parcel.readString();
        walletBalance = parcel.readString();

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(fullName);
        parcel.writeString(transactionId);
        parcel.writeString(transactionDate);
        parcel.writeString(amount);
        parcel.writeString(walletBalance);


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TransactionDetails createFromParcel(Parcel in) {
            return new TransactionDetails(in);
        }

        @Override
        public TransactionDetails[] newArray(int size) {
            return new TransactionDetails[0];
        }


    };
}
