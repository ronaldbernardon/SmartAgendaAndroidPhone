package com.smartagenda.di

import android.content.Context
import androidx.room.Room
import com.smartagenda.data.local.AppDatabase
import com.smartagenda.data.local.EventDao
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.network.SmartAgendaApi
import com.smartagenda.data.repository.SmartAgendaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        preferencesManager: PreferencesManager
    ): Retrofit {
        // Récupérer l'URL depuis les préférences de manière synchrone
        val baseUrl = runBlocking {
            preferencesManager.serverUrl.first().ifEmpty { "http://192.168.1.2:8086/" }
        }
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSmartAgendaApi(retrofit: Retrofit): SmartAgendaApi {
        return retrofit.create(SmartAgendaApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smartagenda_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }
    
    @Provides
    @Singleton
    fun provideSmartAgendaRepository(
        api: SmartAgendaApi,
        eventDao: EventDao,
        preferencesManager: PreferencesManager
    ): SmartAgendaRepository {
        return SmartAgendaRepository(api, eventDao, preferencesManager)
    }
}
