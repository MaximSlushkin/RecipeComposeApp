package com.yourcompany.recipecomposeapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yourcompany.recipecomposeapp.data.database.converter.Converters
import com.yourcompany.recipecomposeapp.data.database.dao.CategoryDao
import com.yourcompany.recipecomposeapp.data.database.entity.CategoryEntity

@Database(
    entities = [CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DATABASE_NAME = "recipes_database"

        @Volatile
        private var INSTANCE: RecipesDatabase? = null

        fun getInstance(context: Context): RecipesDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipesDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        fun buildDatabase(context: Context): RecipesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                RecipesDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}