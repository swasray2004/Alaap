package com.example.musicplayer.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.room.Room
import com.example.musicplayer.data.analyzer.SentimentAnalyzer
import com.example.musicplayer.data.local.MusicDatabase
import com.example.musicplayer.data.remote.LyricsApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSentimentAnalyzer(context: Context): SentimentAnalyzer {
        return SentimentAnalyzer(context)
    }

    @Provides
    fun provideLyricsApiService(): LyricsApiService {
        return Retrofit.Builder()
            .baseUrl("https://your-lyrics-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSongDao(database: MusicDatabase) = database.songDao()

    @Provides
    @Singleton
    fun providePlaylistDao(database: MusicDatabase) = database.playlistDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    @UnstableApi

    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setForceHighestSupportedBitrate(true))
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        return ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .build()
    }
}

