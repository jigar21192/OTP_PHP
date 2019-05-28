package com.sparks.otp_php;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String REGISTER_URL = "https://rentonahd.000webhostapp.com/register.php";
    public static final String CONFIRM_URL = "https://rentonahd.000webhostapp.com/otp.php";

    //Keys to send username, password, phone and otp
    public static final String KEY_USERNAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "mobileno";
    public static final String KEY_OTP = "otp";

    //JSON Tag from response from server
    public static final String TAG_RESPONSE= "ErrorMessage";
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private EditText editTextConfirmOtp;
    String uid;

    private AppCompatButton buttonRegister;
    private AppCompatButton buttonConfirm;

    //Volley RequestQueue
    private RequestQueue requestQueue;
    String msg;
    //String variables to hold username password and phone
    private String username;
    private String password;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.e("GGGG",">>>>>>>"+messageText);
                Toast.makeText(MainActivity.this, messageText, Toast.LENGTH_SHORT).show();
                editTextConfirmOtp.setText(messageText);
            }
        });

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        //Initializing the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        //Adding a listener to button
        buttonRegister.setOnClickListener(this);
    }
        private void confirmOtp() throws JSONException {

            LayoutInflater li = LayoutInflater.from(this);

            View confirmDialog = li.inflate(R.layout.confirm, null);


            buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
            editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);


            AlertDialog.Builder alert = new AlertDialog.Builder(this);


            alert.setView(confirmDialog);


            final AlertDialog alertDialog = alert.create();


            alertDialog.show();


            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();


                    final ProgressDialog loading = ProgressDialog.show(MainActivity.this, "Authenticating", "Please wait while we check the entered code", false,false);



                    final String otp = editTextConfirmOtp.getText().toString().trim();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, CONFIRM_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("uures",">>>>>>"+response);

                                    if(response.trim().equals("success")){

                                        loading.dismiss();

                                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                        //Starting a new activity
                                       // startActivity(new Intent(MainActivity.this, Success.class));
                                    }else{
                                        //Displaying a toast if the otp entered is wrong
                                        Toast.makeText(MainActivity.this,"Wrong OTP Please Try Again",Toast.LENGTH_LONG).show();
                                        try {
                                            //Asking user to enter otp again
                                            confirmOtp();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alertDialog.dismiss();
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            //Adding the parameters otp and username
                            params.put(KEY_OTP, otp);
                            params.put("uid", uid);
                            return params;
                        }
                    };

                    //Adding the request to the queue
                    requestQueue.add(stringRequest);
                }
            });
        }


        //this method will register the user
        private void register() {

            //Displaying a progress dialog
            final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);


            //Getting user data
            username = editTextUsername.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();
            phone = editTextPhone.getText().toString().trim();

            //Again creating the string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("res",">>>>>>"+response);
                            loading.dismiss();
                           if (response.trim().equals("not")){
                               Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                           }else {
                               uid=response.toString();
                               try {
                                   confirmOtp();
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }

                           }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            if (error==null){
                                Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                                try {
                                    confirmOtp();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.e("Err",">>>>>>"+error.getMessage());
                            Toast.makeText(MainActivity.this, error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding the parameters to the request
                    params.put("name", username);
                    params.put("password", password);
                    params.put("mobileno", phone);
                    return params;
                }
            };

            //Adding request the the queue
            requestQueue.add(stringRequest);
        }


        @Override
        public void onClick(View v) {
            //Calling register method on register button click
            register();
        }

    }