package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRiderActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_rider);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtID = findViewById(R.id.txtID);
    }


    /**
     * Called when Add User button is clicked
     * @param v
     */
    public void addNewRider(View v) {
        // get values in form
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String token = "00000000-00000-0000-0000-000000000000";
        String lease = "0000-01-01 00:00:00.000000";
        String role = "rider";
        int is_active =  1 ;
        String secret = "206b2dbe-ecc9-490b-b81b-83767288bc5e";
        String studId = txtID.getText().toString();

        // send request to add new user to the REST API
        UserService userService = ApiUtils.getUserService();
        Call<User> call = userService.addRider(email, username, password,
                token, lease, role, is_active, secret, studId);

        // execute
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // rider added successfully
                    User addedUser = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedUser.getUsername() + " registered successfully.",
                            Toast.LENGTH_LONG).show();
                    // end this activity and go back to previous activity, UserListActivity
                    finish();
                }
                else if (response.code() == 401) {
                    // Registration failed
                    Toast.makeText(getApplicationContext(), "Registration failed: Unauthorized or invalid data.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }


    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
}