package com.myprojects.reciper.di

import android.app.Application
import androidx.room.Room
import com.myprojects.reciper.data.RecipesDatabase
import com.myprojects.reciper.data.RecipesRepository
import com.myprojects.reciper.data.RecipesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipesDatabase(app: Application): RecipesDatabase {
        return Room.databaseBuilder(
            app,
            RecipesDatabase::class.java,
            "recipes"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipesRepository(db: RecipesDatabase): RecipesRepository {
        return RecipesRepositoryImpl(db.dao)
    }
}