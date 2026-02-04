package com.yourcompany.recipecomposeapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.yourcompany.recipecomposeapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY title COLLATE NOCASE ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Int): Flow<RecipeEntity?>

    @Query("SELECT * FROM recipes WHERE category_id = :categoryId ORDER BY title COLLATE NOCASE ASC")
    fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id IN (:recipeIds) ORDER BY title COLLATE NOCASE ASC")
    fun getRecipesByIds(recipeIds: List<Int>): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' ORDER BY title COLLATE NOCASE ASC")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Int)

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes WHERE category_id = :categoryId")
    suspend fun deleteRecipesByCategory(categoryId: Int)

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipesCount(): Int

    @Query("SELECT COUNT(*) FROM recipes WHERE category_id = :categoryId")
    suspend fun getRecipesCountByCategory(categoryId: Int): Int
}