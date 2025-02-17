package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class like extends AppCompatActivity {

    private String id;
    private LinearLayout container;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_like);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent old_intent = getIntent();
        String getName = old_intent.getStringExtra("name");
        String getId = old_intent.getStringExtra("id");
        id = old_intent.getStringExtra("id");
        Toast.makeText(like.this, getId, Toast.LENGTH_SHORT).show();

        container = findViewById(R.id.linearLike);
        db = FirebaseFirestore.getInstance();

        // Récupérer les documents de la collection "users"
        getLikes();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.library);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(getApplicationContext(), acceuil.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId() == R.id.library){
                    Intent intent = new Intent(getApplicationContext(), lib.class);
                    intent.putExtra("name", getName);
                    intent.putExtra("id", getId);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }
    private void getLikes() {
        Toast.makeText(like.this, "Id: " + id, Toast.LENGTH_SHORT).show();
        //.whereEqualTo("user", id)
        db.collection("like")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String like = document.getString("user");
                                creerBoutonsMusic(like);
                            }
                        }
                    }
                });
    }

    private void creerBoutonsMusic(String like) {
        Button button = new Button(this);
        button.setText(like);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(like.this, "Button clicked: " + like, Toast.LENGTH_SHORT).show();
            }
        });

        // Ajouter le bouton au conteneur
        container.addView(button);
    }
}