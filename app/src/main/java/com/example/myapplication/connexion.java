package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.modele.Users;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class connexion extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);


        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Vérification des informations dans Firestore
                db.collection("Users").document(email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Users user = document.toObject(Users.class);
                                    if (user != null && user.getPassword().equals(password)) {
                                        // Redirection vers la page d'accueil
                                        String getName = user.getNom();
                                        user.setId(document.getId());
                                        String getId = user.getId();
                                        //Toast.makeText(connexion.this, user.getId(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(connexion.this, acceuil.class);//accueil.class
                                        intent.putExtra("name", getName);
                                        intent.putExtra("id", getId);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(connexion.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(connexion.this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(connexion.this, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show();
                            }
                        });
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

public class connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connexion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

    }
}*/