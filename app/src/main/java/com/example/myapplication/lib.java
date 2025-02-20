package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class lib extends AppCompatActivity {
    private String id;
    private LinearLayout container;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        // Initialisation de FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        TextView nom = findViewById(R.id.textView3);
        nom.setText(getName);

        id = getId;
        Toast.makeText(lib.this, getId, Toast.LENGTH_SHORT).show();

        container = findViewById(R.id.linearliblike);

        // Récupérer les documents de la collection "like"
        getLikes();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.library);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivityWithExtras(acceuil.class, getName, getId);
                return true;
            } else if (item.getItemId() == R.id.search) {
                startActivityWithExtras(search.class, getName, getId);
                return true;
            } else if (item.getItemId() == R.id.library) {
                startActivityWithExtras(lib.class, getName, getId);
                return true;
            }
            return false;
        });
    }

    private void startActivityWithExtras(Class<?> cls, String name, String id) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void getLikes() {
        Toast.makeText(lib.this, id, Toast.LENGTH_SHORT).show();
        db.collection("like")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String like = document.getString("user");
                            if (like.toLowerCase().contains(id.toLowerCase())) {
                                creerBoutonsMusic(like);
                            }
                        }
                    } else {
                        Toast.makeText(lib.this, "Erreur lors de la récupération des likes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void creerBoutonsMusic(String like) {
        Button button = new Button(this);
        button.setText(like);
        button.setOnClickListener(v -> Toast.makeText(lib.this, "Button clicked: " + like, Toast.LENGTH_SHORT).show());
        container.addView(button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nettoyage des ressources si nécessaire
    }
}
