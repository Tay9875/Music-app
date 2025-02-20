package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        // Construire l'URL d'authentification Spotify
        String authUrl = "https://accounts.spotify.com/authorize" +
                "?client_id=ffd52a25d85c420ca0e36948960e0a8c" +
                "&response_type=code" +
                "&redirect_uri=com.example.myapplication://callback" +
                "&scope=user-read-private%20user-read-email";

        // Rediriger l'utilisateur vers l'URL d'authentification
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        startActivity(intent);


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
                                        Toast.makeText(connexion.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                                        // Redirection vers la page d'accueil
                                        Intent intent = new Intent(connexion.this, acceuil.class);
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