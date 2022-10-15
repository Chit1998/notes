package com.notes.utils;

import android.content.Context;
import android.widget.Toast;


public class Constraint {

    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static String empty = "Empty";
    public static String email_valid = "Email Address is not valid";
    public static String sign_in_success = "Sign in successfully";
    public static String check_credential = "Please check your email and password";
    public static String empty_credential = "Empty credentials";
    public static String password_length = "Please must be write least 8 letters";
    public static String exist_user = "Already registered";
    public static String verify_your_email = "Registration successful\nPlease verify your email address than sign in";
    public static String verify_email_not_sent = "Registration failed\nPlease provide valid email address";
    public static String not_verified = "Your email is not verified";
    public static String reset_mail = "Reset mail has been sent !";
    public static String reset_mail_not = "Reset mail has been not sent. Please provide valid mail.";


    public static void setToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


}
