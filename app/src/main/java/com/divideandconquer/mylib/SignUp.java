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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

	public int MIN_PASSWORD_LENGTH = 6;
	protected boolean registrationStatus;
	private Button signupButton;
	private EditText emailEditText;
	private EditText passwordEditText;
	private EditText libraryCardNumberEditText;
	private FirebaseAuth mAuth;
	private ProgressBar signupProgressBar;
	private DatabaseReference db;

	//not yet working
	private boolean checkLibraryCardRegistrationStatus(final String library_card_number) {
		DatabaseReference readDb = FirebaseDatabase.getInstance().getReference();
		readDb.addValueEventListener(new ValueEventListener() {
			@Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				String status = dataSnapshot.child("Library_cards").child(library_card_number).getValue(String.class);
				if (status.equals("registered")) {
					registrationStatus = true;
				} else {
					registrationStatus = false;
				}
			}

			@Override public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
				registrationStatus = true;
			}
		});

		return registrationStatus;
	}

	private boolean validateUser(String email, String libraryCardNumber, String password) {
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

		//validating library card number
		if (libraryCardNumber.isEmpty()) {
			libraryCardNumberEditText.setError("Library card number can't be empty");
			libraryCardNumberEditText.requestFocus();
			return false;
		} else if (libraryCardNumber.length() != 10) {
			libraryCardNumberEditText.setError("Enter a valid library card number");
			libraryCardNumberEditText.requestFocus();
			return false;
		} else if (checkLibraryCardRegistrationStatus(libraryCardNumber)) {
			libraryCardNumberEditText.setError("Library card already registered");
			libraryCardNumberEditText.requestFocus();
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

	private void saveToUserDatabase(Student currentStudent) {
		String Uid = mAuth.getCurrentUser().getUid();
		db.child("Students").child(Uid).setValue(currentStudent);
	}

	private void registerUser() {
		String email = emailEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		String libraryCardNumber = libraryCardNumberEditText.getText().toString().trim();

		//validate user details
		if (!validateUser(email, libraryCardNumber, password)) {
			return;
		}

		final Student currentStudent = new Student(email, libraryCardNumber);

		signupProgressBar.setVisibility(View.VISIBLE);

		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
			@Override public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					saveToUserDatabase(currentStudent);
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
		db = FirebaseDatabase.getInstance().getReference();

		emailEditText = (EditText) findViewById(R.id.signup_email);
		passwordEditText = (EditText) findViewById(R.id.signup_password);
		libraryCardNumberEditText = (EditText) findViewById(R.id.signup_library_card_number);

		signupButton = (Button) findViewById(R.id.signup_button);
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				registerUser();
			}
		});

		signupProgressBar = (ProgressBar) findViewById(R.id.signup_progressbar);
	}
}
