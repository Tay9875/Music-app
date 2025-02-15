package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myapplication.modele.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialiser Firebase
        FirebaseApp.initializeApp(this);

        // Initialiser Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lire les données depuis Firestore
        db.collection("Users").document("lolita@gmail.com")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Users user = document.toObject(Users.class);
                                Log.d(TAG, "DocumentSnapshot data: " + user);
                                // Afficher les informations de l'utilisateur
                                Toast.makeText(MainActivity.this, "Utilisateur: " + user.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, "No such document");
                                Toast.makeText(MainActivity.this, "Aucun document trouvé", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            Toast.makeText(MainActivity.this, "Échec de la récupération des données", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Configuration des insets pour l'activité principale
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Trouver le bouton de connexion par son ID
        Button buttonConnexion = findViewById(R.id.button3);

        // Définir un listener sur le bouton de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page de connexion", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, connexion.class);
                startActivity(intent);
            }
        });

        // Trouver le bouton d'inscription par son ID
        Button buttonInscription = findViewById(R.id.button);

        // Définir un listener sur le bouton d'inscription
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page d'inscription", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité d'inscription
                Intent intent = new Intent(MainActivity.this, inscription.class);
                startActivity(intent);
            }
        });
    }
}











































/*package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myapplication.modele.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialiser Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lire les données depuis Firestore
        db.collection("Users").document("lolita@gmail.com")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Users user = document.toObject(Users.class);
                                Log.d(TAG, "DocumentSnapshot data: " + user);
                                // Afficher les informations de l'utilisateur
                                Toast.makeText(MainActivity.this, "Utilisateur: " + user.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, "No such document");
                                Toast.makeText(MainActivity.this, "Aucun document trouvé", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            Toast.makeText(MainActivity.this, "Échec de la récupération des données", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Configuration des insets pour l'activité principale
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Trouver le bouton de connexion par son ID
        Button buttonConnexion = findViewById(R.id.button3);

        // Définir un listener sur le bouton de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page de connexion", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, connexion.class);
                startActivity(intent);
            }
        });

        // Trouver le bouton d'inscription par son ID
        Button buttonInscription = findViewById(R.id.button);

        // Définir un listener sur le bouton d'inscription
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page d'inscription", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité d'inscription
                Intent intent = new Intent(MainActivity.this, inscription.class);
                startActivity(intent);
            }
        });
    }
}
*/






























/*package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.modele.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users u = null;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    u = ds.getValue(Users.class);
                }
                Log.d("utilisateur", "Value is: " + u);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("utilisateur", "Failed to read value.", error.toException());
            }
        });

        // Configuration des insets pour l'activité principale
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Trouver le bouton de connexion par son ID
        Button buttonConnexion = findViewById(R.id.button3);

        // Définir un listener sur le bouton de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page de connexion", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, connexion.class);
                startActivity(intent);
            }
        });

        // Trouver le bouton d'inscription par son ID
        Button buttonInscription = findViewById(R.id.button);

        // Définir un listener sur le bouton d'inscription
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page d'inscription", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité d'inscription
                Intent intent = new Intent(MainActivity.this, inscription.class);
                startActivity(intent);
            }
        });
    }
}









































/*package com.example.myapplication;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.modele.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    //private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String value = dataSnapshot.getValue(String.class);
                Users u = null;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    u = ds.getValue(Users.class);
                }
                Log.d("utilisateur", "Value is: " + u.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("utilisateur", "Failed to read value.", error.toException());
            }
        });

        /*Initialisation de Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Envoi d'un événement test pour vérifier la connexion
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_test");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "nom_test");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "type_test");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/


        /* Configuration des insets pour l'activité principale
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /* Vérifier la connexion
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // L'utilisateur est connecté
            Log.d("FirebaseAuth", "User is signed in: " + auth.getCurrentUser().getEmail());
        } else {
            // L'utilisateur n'est pas connecté
            Log.d("FirebaseAuth", "No user is signed in.");
        }*/

        /* Trouver le bouton de connexion par son ID
        Button buttonConnexion = findViewById(R.id.button3);

        // Définir un listener sur le bouton de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page de connexion", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité de connexion
                Intent intent = new Intent(MainActivity.this, connexion.class);
                startActivity(intent);
            }
        });

        // Trouver le bouton d'inscription par son ID
        Button buttonInscription = findViewById(R.id.button);

        // Définir un listener sur le bouton d'inscription
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un Toast
                Toast.makeText(MainActivity.this, "Redirection vers la page d'inscription", Toast.LENGTH_SHORT).show();

                // Créer un Intent pour démarrer la nouvelle activité d'inscription
                Intent intent = new Intent(MainActivity.this, inscription.class);
                startActivity(intent);
            }
        });


    }
}*/