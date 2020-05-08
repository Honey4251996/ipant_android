package com.ipant.activities.home.fragment.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactUsBean {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("errorcode")
    @Expose
    private Integer errorcode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Datum {

        @SerializedName("callus")
        @Expose
        private String callus;
        @SerializedName("emailus")
        @Expose
        private String emailus;
        @SerializedName("website")
        @Expose
        private String website;

        public String getCallus() {
            return callus;
        }

        public void setCallus(String callus) {
            this.callus = callus;
        }

        public String getEmailus() {
            return emailus;
        }

        public void setEmailus(String emailus) {
            this.emailus = emailus;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

    }


}
