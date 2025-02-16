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
import com.google.firebase.firestore.Source
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

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        firestore = Firebase.firestore

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        musicRecyclerView = findViewById(R.id.musicRecyclerView)
        errorTextView = findViewById(R.id.errorTextView)

        musicRecyclerView.layoutManager = LinearLayoutManager(this)

        checkPermissions()

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotBlank()) {
                searchMusic(query)
            } else {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Veuillez saisir un titre de musique."
                musicRecyclerView.visibility = View.GONE
            }
        }

        // Désactiver le cache Firestore pour forcer le rafraîchissement des données
        firestore.clearPersistence()

        // Activer les logs Firestore
        FirebaseFirestore.setLoggingEnabled(true)
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = "Erreur lors de la récupération des musiques : ${error.message}"
                    Log.e(TAG, "Erreur Firestore : ", error)
                    return@addSnapshotListener
                }

                val musicList = documents?.mapNotNull { doc ->
                    Log.d(TAG, "Document Firestore récupéré : ${doc.id} -> ${doc.data}")
                    doc.toObject<Music>()
                }?.filter { music ->
                    music.title.contains(query, ignoreCase = true)
                } ?: emptyList()

                if (musicList.isNotEmpty()) {
                    musicRecyclerView.adapter = MusicAdapter(musicList)
                    musicRecyclerView.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                } else {
                    musicRecyclerView.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = "Aucune musique trouvée pour \"$query\""
                }

                Log.d(TAG, "Nombre de musiques trouvées : ${musicList.size}")
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
                    exoPlayer?.release()
                    exoPlayer = null

                    try {
                        Log.d(TAG, "Lecture de la musique : ${music.url}")

                        if (music.url.isBlank()) {
                            Toast.makeText(itemView.context, "URL de la musique non valide", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }

                        exoPlayer = ExoPlayer.Builder(itemView.context).build().apply {
                            val mediaItem = MediaItem.fromUri(music.url)
                            setMediaItem(mediaItem)
                            prepare()
                            play()

                            addListener(object : Player.Listener {
                                override fun onPlayerError(error: PlaybackException) {
                                    Log.e(TAG, "Erreur de lecture : ${error.errorCodeName}")
                                    Toast.makeText(itemView.context, "Erreur lors de la lecture : ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }

                                override fun onPlaybackStateChanged(state: Int) {
                                    when (state) {
                                        Player.STATE_BUFFERING -> Log.d(TAG, "Mise en mémoire tampon...")
                                        Player.STATE_READY -> Log.d(TAG, "Lecture prête")
                                        Player.STATE_ENDED -> {
                                            Log.d(TAG, "Lecture terminée")
                                            exoPlayer?.release()
                                            exoPlayer = null
                                        }
                                        Player.STATE_IDLE -> Log.d(TAG, "Lecteur à l'état IDLE")
                                    }
                                }
                            })
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur lors de l'initialisation du lecteur : ${e.message}")
                        Toast.makeText(itemView.context, "Erreur lors de l'initialisation du lecteur : ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            fun releasePlayer() {
                exoPlayer?.release()
                exoPlayer = null
            }
        }
    }
}
