package com.tigcal.emailvalidator;

public class Kickbox {

    public static final String BASE_URL = "https://api.kickbox.com/v2/verify";
    //TODO get API Key from Kickbox: https://docs.kickbox.com/docs/using-the-api
    public static final String API_KEY = "";

    //Kickbox Parameters
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_API_KEY = "apikey";

    //Kickbox Response
    public static final String RESPONSE_DELIVERABLE = "deliverable";

    //Kickbox Output Variables
    public static final String OUTPUT_REASON = "reason";
    public static final String OUTPUT_RESULT= "result";

    //Kickbox Response Reasons
    public static final String REASON_INVALID_EMAIL = "invalid_email";
    public static final String REASON_INVALID_DOMAIN = "invalid_domain";
    public static final String REASON_REJECTED_EMAIL = "rejected_email";
    public static final String REASON_ACCEPTED_EMAIL = "accepted_email";
    public static final String REASON_LOW_QUALITY = "low_quality";
    public static final String REASON_LOW_DELIVERABILITY = "low_deliverability";
    public static final String REASON_NO_CONNECT = "no_connect";
    public static final String REASON_TIMEOUT = "timeout";
    public static final String REASON_INVALID_SMTP = "invalid_smtp";
    public static final String REASON_UNAVAILABLE_SMTP = "unavailable_smtp";
    public static final String REASON_UNEXPECTED_ERROR = "unexpected_error";
}
