package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliffcorp.car2pool.model.FailLogin;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDriverFragment extends Fragment {

    private EditText txtFullName, txtEmail, txtUsername, txtPassword, txtID, txtCarModel, txtPlateNumber, txtLicense;
    private Button btnNewDriver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        // Bind unique Driver field view references
        txtFullName = view.findViewById(R.id.txtDriverFullName);
        txtUsername = view.findViewById(R.id.txtDriverUsername);
        txtEmail = view.findViewById(R.id.txtDriverEmail);
        txtPassword = view.findViewById(R.id.txtDriverPassword);
        txtID = view.findViewById(R.id.txtDriverID);
        txtCarModel = view.findViewById(R.id.txtCarModel);
        txtPlateNumber = view.findViewById(R.id.txtPlateNumber);
        txtLicense = view.findViewById(R.id.txtLicense);
        btnNewDriver = view.findViewById(R.id.btnNewDriver);

        btnNewDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDriver();
            }
        });

        return view;
    }

    private void addNewDriver() {
        String api_key = "3b191b07-7739-4698-b885-8660b564c963";
        String fullName = txtFullName.getText().toString();
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();

        NewUserActivity activity = (NewUserActivity) getActivity();
        if (activity == null) return;

        String password = activity.md5(txtPassword.getText().toString());
        String token = "00000000-00000-0000-0000-000000000000";
        String lease = "1970-01-01 00:00:00";
        String role = "driver"; // Swapped to role driver
        int is_active = 1;
        String secret = "206b2dbe-ecc9-490b-b81b-83767288bc5e";
        String studId = txtID.getText().toString();
        String carModel = txtCarModel.getText().toString();
        String plateNumber = txtPlateNumber.getText().toString();
        String license = txtLicense.getText().toString();

        if (validateRegister(fullName, username, password, email, studId, carModel, plateNumber, license)) {
            doNewDriver(api_key, email, username, password, token, lease, role, is_active,
                    secret, studId, carModel, plateNumber, license, fullName);
        }
    }

    private void doNewDriver(String api_key, String email, String username, String password, String token,
                             String lease, String role, int is_active, String secret, String studId,
                             String carModel, String plateNumber, String license, String fullName) {
        UserService userService = ApiUtils.getUserService();

        // Ensure you adapt this to your actual backend signature within your UserService interface
        Call<User> call = userService.addUser(api_key, email, username, password, token, lease, role,
                is_active, secret, studId, carModel, plateNumber, license, fullName);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null && user.getToken() != null) {
                        displayToast("Register successful");

                        if (getActivity() != null) {
                            SharedPrefManager spm = new SharedPrefManager(getActivity().getApplicationContext());
                            spm.storeUser(user);

                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        displayToast("Register error");
                    }
                } else {
                    try {
                        String errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp", e.toString());
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error connecting to server.");
                Log.e("MyApp", t.toString());
            }
        });
    }

    private boolean validateRegister(String fullName, String username, String password, String email, String studId, String carModel, String plateNumber, String license) {
        if (fullName == null || fullName.trim().isEmpty()) {
            txtFullName.setError("Full Name is required");
            txtFullName.requestFocus();
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            txtUsername.setError("Username is required");
            txtUsername.requestFocus();
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
            return false;
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            txtEmail.setError("Enter a valid email");
            txtEmail.requestFocus();
            return false;
        }
        if (studId == null || studId.trim().isEmpty()) {
            txtID.setError("Student ID is required");
            txtID.requestFocus();
            return false;
        }
        if (carModel == null || carModel.trim().isEmpty()) {
            txtCarModel.setError("Car Model is required");
            txtCarModel.requestFocus();
            return false;
        }
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            txtPlateNumber.setError("Plate Number is required");
            txtPlateNumber.requestFocus();
            return false;
        }
        if (license == null || license.trim().isEmpty()) {
            txtLicense.setError("License payload is required");
            txtLicense.requestFocus();
            return false;
        }
        return true;
    }

    private void displayToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}