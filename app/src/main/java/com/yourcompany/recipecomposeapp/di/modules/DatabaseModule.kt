package com.yourcompany.recipecomposeapp.di.modules

import android.content.Context
import com.yourcompany.recipecomposeapp.data.database.RecipesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipesDatabase(
        @ApplicationContext context: Context
    ): RecipesDatabase {
        return RecipesDatabase.buildDatabase(context)
    }
}