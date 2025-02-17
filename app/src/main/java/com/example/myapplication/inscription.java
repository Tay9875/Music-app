package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.modele.Users;
import com.google.firebase.firestore.FirebaseFirestore;

public class inscription extends AppCompatActivity {

    private EditText enterName, emailInput, passwordInput, confirmPasswordInput;
    private Button loginButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        enterName = findViewById(R.id.enter_name);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.enter_confirmpassword);
        loginButton = findViewById(R.id.login_button);

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = enterName.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (password.equals(confirmPassword)) {
                    Users user = new Users(name, email, password);

                    // Enregistrement des données dans Firestore
                    db.collection("Users").document(email)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                // Redirection vers la page de connexion
                                Intent intent = new Intent(inscription.this, connexion.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(inscription.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(inscription.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}






























/*package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.modele.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class inscription extends AppCompatActivity {

    private EditText enterName, emailInput, passwordInput, confirmPasswordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        enterName = findViewById(R.id.enter_name);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.enter_confirmpassword);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = enterName.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (password.equals(confirmPassword)) {
                    Users user = new Users(name, email, password);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users");
                    myRef.push().setValue(user);

                    Toast.makeText(inscription.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(inscription.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}























/*package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class inscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}*/