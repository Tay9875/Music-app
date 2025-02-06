package com.example.music

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firestore: FirebaseFirestore
    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        // Initialiser Firestore
        firestore = Firebase.firestore

        // Récupérer les références des vues depuis le layout
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        musicRecyclerView = findViewById(R.id.musicRecyclerView)
        errorTextView = findViewById(R.id.errorTextView)

        // Configurer le RecyclerView avec un LinearLayoutManager
        musicRecyclerView.layoutManager = LinearLayoutManager(this)

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

    private fun searchMusic(query: String) {
        Log.d("MainActivity", "Recherche de musique avec le titre : $query")
        firestore.collection("music")
            .get()
            .addOnSuccessListener { documents ->
                val musicList = documents.map { doc ->
                    doc.toObject(Music::class.java)
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
                Log.d("MainActivity", "Nombre de musiques trouvées : ${musicList.size}")
            }
            .addOnFailureListener { exception ->
                // Gérer l'erreur
                musicRecyclerView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "Erreur lors de la recherche : ${exception.message}"
                Log.e("MainActivity", "Erreur lors de la recherche", exception)
            }
    }

    data class Music(val title: String = "", val url: String = "")

    class MusicAdapter(private val musicList: List<Music>) :
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
                    // Libérer l'instance précédente d'ExoPlayer si elle existe
                    exoPlayer?.release()
                    exoPlayer = null

                    // Initialiser ExoPlayer et lire la musique
                    exoPlayer = ExoPlayer.Builder(itemView.context).build().apply {
                        val mediaItem = MediaItem.fromUri(music.url)
                        setMediaItem(mediaItem)
                        prepare()
                        play()
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
