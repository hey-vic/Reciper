package com.myprojects.reciper.di

import android.app.Application
import androidx.room.Room
import com.myprojects.reciper.data.RecipeDatabase
import com.myprojects.reciper.data.RecipeRepository
import com.myprojects.reciper.data.RecipeRepositoryImpl
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
    fun provideRecipesDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            "recipes"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipesRepository(db: RecipeDatabase): RecipeRepository {
        return RecipeRepositoryImpl(db.dao)
    }
}