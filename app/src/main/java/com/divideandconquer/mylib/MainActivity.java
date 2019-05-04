package com.divideandconquer.mylib;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

	FirebaseAuth mAuth;
	private Button signupButton;
	private Button loginButton;
	private EditText emailEditText;
	private EditText passwordEditText;
	private ProgressBar loginProgressBar;

	private boolean validateUser(String email, String password) {
		if (email.isEmpty()) {
			emailEditText.setError("Enter email");
			emailEditText.requestFocus();
			return false;
		}
		if (password.isEmpty()) {
			passwordEditText.setError("Enter password");
			passwordEditText.requestFocus();
			return false;
		}
		return true;
	}

	private void launchUserProfie() {
		Intent i = new Intent(MainActivity.this, ProfileActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	private void userLogin() {
		String email = emailEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();

		if (!validateUser(email, password)) {
			return;
		}

		loginProgressBar.setVisibility(View.VISIBLE);

		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					loginButton.setVisibility(View.GONE);
					launchUserProfie();
				} else {
					loginButton.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG);
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mAuth = FirebaseAuth.getInstance();
		//launch profile if already logged in
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			launchUserProfie();
		}

		setContentView(R.layout.activity_main);

		signupButton = (Button) findViewById(R.id.login_sign_up_button);
		loginButton = (Button) findViewById(R.id.login_button);
		emailEditText = (EditText) findViewById(R.id.login_email);
		passwordEditText = (EditText) findViewById(R.id.login_password);
		loginProgressBar = (ProgressBar) findViewById(R.id.login_progressbar);



		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), SignUp.class);
				startActivity(i);
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				userLogin();
			}
		});
	}
}
