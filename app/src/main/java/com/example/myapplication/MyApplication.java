package com.example.myapplication;

import android.app.Application;
import com.google.firebase.FirebaseApp;
/*import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;*/

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialisation de Firebase
        FirebaseApp.initializeApp(this);
    }
}
