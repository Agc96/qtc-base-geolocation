// Check current auth state

private FirebaseAuth mAuth;
mAuth = FirebaseAuth.getInstance();

@Override
public void onStart() {
	super.onStart();
	// Check if user is signed in (non-null) and update UI accordingly.
	FirebaseUser currentUser = mAuth.getCurrentUser();
	updateUI(currentUser);
}

// Sign up new users

mAuth.createUserWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "createUserWithEmail:success");
					FirebaseUser user = mAuth.getCurrentUser();
					updateUI(user);
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "createUserWithEmail:failure", task.getException());
					Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
							Toast.LENGTH_SHORT).show();
					updateUI(null);
				}

				// ...
			}
		});

// Sign in existing users

mAuth.signInWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "signInWithEmail:success");
					FirebaseUser user = mAuth.getCurrentUser();
					updateUI(user);
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "signInWithEmail:failure", task.getException());
					Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
							Toast.LENGTH_SHORT).show();
					updateUI(null);
				}

				// ...
			}
		});

// Access user information

FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
if (user != null) {
    // Name, email address, and profile photo Url
    String name = user.getDisplayName();
    String email = user.getEmail();
    Uri photoUrl = user.getPhotoUrl();

    // Check if user's email is verified
    boolean emailVerified = user.isEmailVerified();

    // The user's ID, unique to the Firebase project. Do NOT use this value to
    // authenticate with your backend server, if you have one. Use
    // FirebaseUser.getIdToken() instead.
    String uid = user.getUid();
}

///////////////////////////////////////////////////////////////////////////////////////////////////

// Write a message to the database
FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference myRef = database.getReference("message");

myRef.setValue("Hello, World!");

// Read from the database
myRef.addValueEventListener(new ValueEventListener() {
	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		// This method is called once with the initial value and again
		// whenever data at this location is updated.
		String value = dataSnapshot.getValue(String.class);
		Log.d(TAG, "Value is: " + value);
	}

	@Override
	public void onCancelled(DatabaseError error) {
		// Failed to read value
		Log.w(TAG, "Failed to read value.", error.toException());
	}
});

