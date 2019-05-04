package com.divideandconquer.mylib;

import android.media.MediaCodec;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {

	public int MIN_PASSWORD_LENGTH = 6;
	private Button signupButton;
	private EditText emailEditText;
	private EditText passwordEditText;
	private FirebaseAuth mAuth;
	private ProgressBar signupProgressBar;

	private boolean validateUser(String email, String password) {
		//validating email
		if (email.isEmpty()) {
			emailEditText.setError("Email can't be empty");
			emailEditText.requestFocus();
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			emailEditText.setError("Enter a valid email");
			emailEditText.requestFocus();
			return false;
		}

		//validating password
		if (password.isEmpty()) {
			passwordEditText.setError("Enter your password");
			passwordEditText.requestFocus();
			return false;
		} else if (password.length() < MIN_PASSWORD_LENGTH) {
			passwordEditText.setError("Minimum password length should be six");
			passwordEditText.requestFocus();
			return false;
		}

		//return true if all tests are passed
		return true;
	}

	private void registerUser() {
		String email = emailEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();

		//validate user details
		if (!validateUser(email, password)) {
			return;
		}

		signupProgressBar.setVisibility(View.VISIBLE);

		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					signupProgressBar.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_SHORT).show();
				} else {
					signupProgressBar.setVisibility(View.GONE);
					if (task.getException() instanceof FirebaseAuthUserCollisionException) {
						Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "Registration unsuccessful", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		mAuth = FirebaseAuth.getInstance();

		emailEditText = (EditText) findViewById(R.id.signup_email);
		passwordEditText = (EditText) findViewById(R.id.signup_password);

		signupButton = (Button) findViewById(R.id.signup_button);
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				registerUser();
			}
		});

		signupProgressBar = (ProgressBar) findViewById(R.id.signup_progressbar);
	}
}
