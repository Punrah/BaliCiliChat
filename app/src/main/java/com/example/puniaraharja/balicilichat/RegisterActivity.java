
package com.example.puniaraharja.balicilichat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puniaraharja.balicilichat.app.Formater;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputPassword;

    TextView termOfService;
    TextView privacyPolicy;

    private TextView countryCode;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (TextView) findViewById(R.id.btnLinkToLoginScreen);
        countryCode = (TextView) findViewById(R.id.country_code);
        countryCode.setText("+"+GetCountryZipCode().toString()+" ");

        termOfService= (TextView) findViewById(R.id.term_of_service);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();
                String formatedPhone = Formater.phoneNumber(inputPhone.getText().toString().trim(),GetCountryZipCode());
                String password = inputPassword.getText().toString().trim();
                createAccount(email,password);

            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                privacyPolicy.startAnimation(animation1);
                Intent i = new Intent(RegisterActivity.this,WebActivity.class);
                i.putExtra("title", "Ketentuan Layanan");
                i.putExtra("action","term_of_service");
                startActivity(i);

            }
        });
        termOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                termOfService.startAnimation(animation1);
                Intent i = new Intent(RegisterActivity.this,WebActivity.class);
                i.putExtra("title", "Kebijakan Privasi");
                i.putExtra("action","privacy_policy");
                startActivity(i);
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User();
                            user.id =firebaseUser.getUid();
                            user.email=firebaseUser.getEmail();
                            user.name=inputFullName.getText().toString().trim();
                            user.image =inputPhone.getText().toString().trim();
                            mDatabase.child("users").child(user.id).setValue(user);
                            Intent intent = new Intent(RegisterActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = false;




        String name = inputFullName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String formatedPhone = Formater.phoneNumber(inputPhone.getText().toString().trim(),GetCountryZipCode());
        String password = inputPassword.getText().toString().trim();

        if(name.isEmpty())
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.empty_name_pop_up));
        }
        else if(email.isEmpty())
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.empty_email_pop_up));
        }
        else if(!Formater.isValidEmailAddress(email))
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.email_format_pop_up));
        }
        else if(phone.isEmpty())
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.empty_phone_pop_up));
        }
        else if(phone.length()<8)
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.phone_format_pop_up));
        }
        else if(password.isEmpty())
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.empty_password_pop_up));
        }
        else if(password.length()<8)
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.minimum_characters_error_messages));
        }
        else if(!password.matches("[A-Za-z0-9]+"))
        {
            Formater.viewDialog(RegisterActivity.this,getString(R.string.must_be_alphanumerics_error_messages));
        }
        else
        {
            valid=true;
        }

        return valid;
    }




    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl= getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }


}
