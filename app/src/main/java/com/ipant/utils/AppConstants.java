package com.ipant.utils;

import com.ipant.activities.login.bean.UserDataBean;

public class AppConstants {

    //   public static final String BASE_URL="https://bizxpay.consagous.co.in/webservices/";

  //  public static final String BASE_URL = "http://admin.ipant.se/webservices/";
    public static final String BASE_URL = "https://admin.ipant.se/webservices/v1/";

    //    public static final String BASE_URL = "http://devadmin.ipant.se/webservices/v1/";

    public static final String GET_COUNTRIES_URL = BASE_URL + "users/get_countries_list";

    public static final String USER_LOGIN_URL = BASE_URL + "users/login";

    public static final String USER_SIGN_UP_OR_FORGOT_PASSWORD = BASE_URL + "users/forgot_password";

    public static final String USER_RESEND_OTP_URL = BASE_URL + "users/resend_otp";

    public static final String OTP_VERIFICATION = BASE_URL + "users/varify_otp";

    public static final String COMPLETE_SIGN_UP_URL = BASE_URL + "users/register";

    public static final String RESET_PASSWORD_URL = BASE_URL + "users/reset_password";

    public static final String ABOUT_APP_URL = BASE_URL + "users/about_app";

    public static final String PRIVACY_POLICY_URL = BASE_URL + "users/privacy_policy";

    public static final String TERMNS_AND_CONDITIONS_URL = BASE_URL + "users/terms_condition";

    public static final String CONTACT_US_URL = BASE_URL + "users/contact_us";

    public static final String FAQ_URL = BASE_URL + "users/faq";

    public static final String UPDATE_NOTIFICATION_STATUS_URL = BASE_URL + "users/change_notification_status";

    public static final String CHANGE_PASSWORD_URL = BASE_URL + "users/changepassword";

    public static final String GET_CARD_DETAILS = BASE_URL + "depositmoney/getCardDetails";

    public static final String SEND_MONEY_URL = BASE_URL + "depositmoney/sendMoney";

    public static final String ADD_MONEY_URL = BASE_URL + "depositmoney/addMoney";

    public static final String WITHDRAW_MONEY_URL = BASE_URL + "depositmoney/withdrawMoney";

    public static final String CHECK_USER_EXISTANCE_URL = BASE_URL + "users/userExist";

    public static final String CHECK_CURRENT_WALLET_BALANCE = BASE_URL + "users/get_wallet_balance";

    public static final String GET_USER_DETAILS_URL = BASE_URL + "users/get_profile";

    public static final String UPDATE_USER_PROFILE_URL = BASE_URL + "users/profile_update";

    public static final String GET_TRANSACTION_HISTORY_URL = BASE_URL + "depositmoney/get_transaction_history";

    public static final String DELETE_CARD_OR_BANK_URL = BASE_URL + "depositmoney/deleteCardDetail";

    public static final String ADDMONEYUSINGNETBANKING = BASE_URL + "depositmoney/addMoneyUsingNetBanking";

    public static final String WITHDRAWMONEYUSINGNETBANKING = BASE_URL + "depositmoney/withdrawMoneyUsingNetBanking";

    public static final String DONATE_MONEY_URL=BASE_URL+"depositmoney/donationMoney";


    /**
     * FOR THE QR CODE BASE URL IS DIFFERENT
     */
    //private static final String QR_CODE_BASE = "http://admin.ipant.se/";
    private static final String QR_CODE_BASE = "https://admin.ipant.se/";

    public static final String QR_CODE_SCAN_API = QR_CODE_BASE + "ipant/soap/payout.php?sessionId=";


    // Keys
    public static String KEY_USER_DATA = "key_user_data";
    public static final String KEY_GET_STARTED_COMPLETED = "key_get_started_completed";
    public static UserDataBean USER_DATA_BEAN_OBJ = null;
    public static String DEFAULT_TIMEZONE = "";
    public static String LANGUAGE = "sw";
    public static String FCM_TOKEN = "";
    public static String WALLET_AMOUNT = "0.00";
}
