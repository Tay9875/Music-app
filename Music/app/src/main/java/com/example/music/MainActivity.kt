package com.example.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.PlaybackException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 1

    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var errorTextView: TextView

    // Déclarez firestore comme variable de classe pour y accéder dans toutes les fonctions
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assurez-vous que le fichier activity_main.xml est correctement configuré
        setContentView(R.layout.activity_main)

        // Initialiser Firebase Analytics (si nécessaire)
        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        // Initialiser Firestore
        firestore = Firebase.firestore

        // Récupérer les références des vues depuis le layout
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        musicRecyclerView = findViewById(R.id.musicRecyclerView)
        errorTextView = findViewById(R.id.errorTextView)

        // Configurer le RecyclerView avec un LinearLayoutManager
        musicRecyclerView.layoutManager = LinearLayoutManager(this)

        // Vérifier les permissions
        checkPermissions()

        // Configurer le bouton de recherche
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotBlank()) {
                searchMusic(query)
            } else {
                // Afficher un message si le champ de recherche est vide
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Veuillez saisir un titre de musique."
                musicRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.INTERNET)
        }

        // Pour les versions Android 6.0 et supérieures, READ_EXTERNAL_STORAGE n'est plus nécessaire pour accéder à Internet
        // Supprimez la vérification si vous n'accédez pas au stockage externe
        /*
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        */

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Permission ${permissions[i]} refusée, l'application risque de ne pas fonctionner correctement.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun searchMusic(query: String) {
        Log.d(TAG, "Recherche de musique avec le titre : $query")
        firestore.collection("music")
            .get()
            .addOnSuccessListener { documents ->
                val musicList = documents.mapNotNull { doc ->
                    doc.toObject<Music>()
                }.filter { music ->
                    music.title.contains(query, ignoreCase = true)
                }

                if (musicList.isNotEmpty()) {
                    musicRecyclerView.adapter = MusicAdapter(musicList)
                    musicRecyclerView.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                } else {
                    // Aucun résultat trouvé
                    musicRecyclerView.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = "Aucune musique trouvée pour \"$query\""
                }
                Log.d(TAG, "Nombre de musiques trouvées : ${musicList.size}")
            }
            .addOnFailureListener { exception ->
                // Gérer l'erreur
                musicRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Erreur lors de la recherche : ${exception.message}"
                Log.e(TAG, "Erreur lors de la recherche", exception)
            }
    }

    data class Music(val title: String = "", val url: String = "")

    inner class MusicAdapter(private val musicList: List<Music>) :
        RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_music, parent, false)
            return MusicViewHolder(view)
        }

        override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
            val music = musicList[position]
            holder.bind(music)
        }

        override fun getItemCount(): Int = musicList.size

        override fun onViewRecycled(holder: MusicViewHolder) {
            super.onViewRecycled(holder)
            holder.releasePlayer()
        }

        inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            private val playButton: Button = itemView.findViewById(R.id.playButton)
            private var exoPlayer: ExoPlayer? = null

            fun bind(music: Music) {
                titleTextView.text = music.title

                playButton.setOnClickListener {
                    // Libérer le précédent lecteur
                    exoPlayer?.release()
                    exoPlayer = null

                    try {
                        // Afficher l'URL pour débogage
                        Log.d("MusicAdapter", "URL de la musique : ${music.url}")

                        // Vérifier si l'URL est valide
                        if (music.url.isBlank()) {
                            Toast.makeText(
                                itemView.context,
                                "URL de la musique non valide",
                                Toast.LENGTH_LONG
                            ).show()
                            return@setOnClickListener
                        }

                        // Initialiser ExoPlayer et lire la musique depuis l'URL
                        exoPlayer = ExoPlayer.Builder(itemView.context).build().apply {
                            val mediaItem = MediaItem.fromUri(music.url)
                            setMediaItem(mediaItem)
                            prepare()
                            play()

                            addListener(object : Player.Listener {
                                override fun onPlayerError(error: PlaybackException) {
                                    Log.e("MusicAdapter", "Erreur de lecture : ${error.errorCodeName}")
                                    Toast.makeText(
                                        itemView.context,
                                        "Erreur lors de la lecture : ${error.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                override fun onPlaybackStateChanged(state: Int) {
                                    when (state) {
                                        Player.STATE_BUFFERING -> Log.d("MusicAdapter", "Mise en mémoire tampon...")
                                        Player.STATE_READY -> Log.d("MusicAdapter", "Lecture prête")
                                        Player.STATE_ENDED -> {
                                            Log.d("MusicAdapter", "Lecture terminée")
                                            exoPlayer?.release()
                                            exoPlayer = null
                                        }
                                        Player.STATE_IDLE -> Log.d("MusicAdapter", "Lecteur à l'état IDLE")
                                    }
                                }
                            })
                        }

                        Log.d("MusicAdapter", "Lecture de la musique : ${music.url}")

                    } catch (e: Exception) {
                        Log.e("MusicAdapter", "Erreur lors de l'initialisation du lecteur : ${e.message}")
                        Toast.makeText(
                            itemView.context,
                            "Erreur lors de l'initialisation du lecteur : ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            fun releasePlayer() {
                exoPlayer?.release()
                exoPlayer = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libérer ExoPlayer pour chaque ViewHolder
        val adapter = musicRecyclerView.adapter as? MusicAdapter
        adapter?.let {
            for (i in 0 until it.itemCount) {
                val holder = musicRecyclerView.findViewHolderForAdapterPosition(i) as? MusicAdapter.MusicViewHolder
                holder?.releasePlayer()
            }
        }
    }
}
