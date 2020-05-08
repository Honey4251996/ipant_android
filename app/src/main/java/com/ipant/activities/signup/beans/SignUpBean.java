package com.ipant.activities.signup.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SignUpBean {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("errorcode")
    @Expose
    private Integer errorcode;
    @SerializedName("data")
    @Expose
    private List<DataInfo> data = null;
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

    public List<DataInfo> getData() {
        return data;
    }

    public void setData(List<DataInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class DataInfo {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("varification_type")
        @Expose
        private String varificationType;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getVarificationType() {
            return varificationType;
        }

        public void setVarificationType(String varificationType) {
            this.varificationType = varificationType;
        }

    }
}
