package com.lucian.garliclike

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import kotlin.random.Random

// 'by lazy' is a Kotlin feature that means the variable 'musicTracks' is not created and initialized until it is first accessed.
class Level1Music {
    private val musicTracks: List<Music> by lazy {
        listOf(
            // Each call to 'Gdx.audio.newMusic' loads a music track from the specified file.
            // These tracks are stored in a list.
            Gdx.audio.newMusic(Gdx.files.internal("assets/music/xDeviruchi - And The Journey Begins.wav")),
            Gdx.audio.newMusic(Gdx.files.internal("assets/music/xDeviruchi - Prepare for Battle!.wav")),
            Gdx.audio.newMusic(Gdx.files.internal("assets/music/xDeviruchi - Decisive Battle.wav"))
        )
    }
    // This variable keeps track of which music track is currently playing.
    private var currentTrackIndex = 0

    // 'playRandomTrack' method stops the currently playing track and plays a random one.
    fun playRandomTrack() {
        musicTracks[currentTrackIndex].stop() // Stop the current track.
        currentTrackIndex = Random.nextInt(musicTracks.size) // Choose a random track index.
        musicTracks[currentTrackIndex].apply {
            volume = 0.3f
            isLooping = true
            play()
        }
    }

    // 'stopMusic' method stops the currently playing music track.
    fun stopMusic() {
        musicTracks[currentTrackIndex].stop()
    }

    // 'dispose' method releases all resources used by the music tracks.
    fun dispose() {
        musicTracks.forEach { it.dispose() }
    }
}
