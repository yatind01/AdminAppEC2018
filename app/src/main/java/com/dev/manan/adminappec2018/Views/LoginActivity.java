package com.dev.manan.adminappec2018.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dev.manan.adminappec2018.R;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private String usr, pass;
    private Button login;
    private ImageView photoImageView;
    SharedPreferences prefs;
    private TextView devByTextView;
    private int unicode = 0x2764;
    private String CLUB_NAME = "clubname";
    private ProgressDialog pd;
    private JSONObject jObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.loginButton);
        photoImageView = findViewById(R.id.iv_login_photo);
        devByTextView = findViewById(R.id.tv_developed_by_text);

        String s1 = "Developed with ";
        String s2 = " by ";
        String s3 = "<b> MANAN </b>";
        devByTextView.setText(s1 + getEmojiByUnicode(unicode) + s2 + Html.fromHtml(s3));

        prefs = getApplicationContext().getSharedPreferences("com.dev.manan.adminappec2018", Context.MODE_PRIVATE);

        Picasso.get().load(R.drawable.background).fit().into(photoImageView);

        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Authenticating...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    ClubSignIn();
                } else {
                    MDToast.makeText(LoginActivity.this, "Connect to internet!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }
            }
        });

// Code for on Launch checker --- If Login or not!
        String restoredUsername = prefs.getString("username", null);
        if (restoredUsername != null) {
            Intent intent = new Intent(LoginActivity.this, BrixxActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    private void ClubSignIn() {
        usr = username.getText().toString().trim();
        pass = password.getText().toString().trim();

        if (usr.isEmpty()) {
            username.setError("Enter username!");
            return;
        }

        if (pass.isEmpty()) {
            password.setError("Enter password!");
            return;
        }

        pd.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://elementsculmyca2018.herokuapp.com/api/v1/admin/login/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            jObject = new JSONObject(response);

                            if (jObject.getInt("success") == 1) {
                                prefs.edit().putString("token", jObject.getString("token"))
                                        .putString("username", jObject.getString("username"))
                                        .apply();

                                if (jObject.getString("username").equals("Brixx") || jObject.getString("username").equals("uadmin")) {
                                    showDialogBox(0);
                                } else {
                                    showDialogBox(1);
                                }
                            } else {
                                MDToast.makeText(LoginActivity.this, "Wrong credentials", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        MDToast.makeText(LoginActivity.this, "Error Login!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", usr);
                params.put("password", pass);
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    private void showDialogBox(final int val) {
        final EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        final LinearLayout layout = new LinearLayout(LoginActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();
        alert.setTitle("Enter Secret Key");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 0, 30, 0);

        input.setLayoutParams(layoutParams);

        layout.addView(input);

        alert.setView(layout).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals("calm")){
                    prefs.edit().putString("status", "calm").apply();
                    if(val == 0) {
                        Intent intent = new Intent(LoginActivity.this, BrixxActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, OtherClubsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else if(input.getText().toString().equals("chaos")){
                    prefs.edit().putString("status", "chaos").apply();
                    if(val == 0) {
                        Intent intent = new Intent(LoginActivity.this, BrixxActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, OtherClubsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        LoginActivity.this.finish();
        System.exit(0);
    }
}


