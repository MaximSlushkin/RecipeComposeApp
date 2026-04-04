package com.yourcompany.recipecomposeapp.features.core.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.yourcompany.recipecomposeapp.core.utils.FavoriteDataStoreManager
import com.yourcompany.recipecomposeapp.core.utils.dataStore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDataStoreTest {

    private lateinit var context: Context
    private lateinit var manager: FavoriteDataStoreManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = FavoriteDataStoreManager(context)
    }

    @After
    fun tearDown() = runTest {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    @Test
    fun addFavoriteSavesRecipeId() = runTest {
        manager.addFavorite(42)

        val favorites = manager.getAllFavorites()
        assertTrue(favorites.contains("42"))
        assertTrue(manager.isFavorite(42))
    }

    @Test
    fun addMultipleFavoritesSavesAllIds() = runTest {
        val recipeIds = listOf(1, 2, 3, 4, 5)
        recipeIds.forEach { manager.addFavorite(it) }

        val favorites = manager.getAllFavorites()
        assertEquals(5, favorites.size)
        recipeIds.forEach { id ->
            assertTrue(favorites.contains(id.toString()))
        }
    }

    @Test
    fun removeFromFavoritesDeletesRecipeId() = runTest {
        manager.addFavorite(100)
        assertTrue(manager.isFavorite(100))

        manager.removeFavorite(100)

        assertFalse(manager.isFavorite(100))
        assertTrue(manager.getAllFavorites().isEmpty())
    }

    @Test
    fun toggleFavoriteSwitchesStateCorrectly() = runTest {
        val recipeId = 200
        assertFalse(manager.isFavorite(recipeId))

        manager.toggleFavorite(recipeId)
        assertTrue(manager.isFavorite(recipeId))

        manager.toggleFavorite(recipeId)
        assertFalse(manager.isFavorite(recipeId))
    }

    @Test
    fun favoritesFlowEmitsUpdatesReactively() = runTest {
        manager.getFavoriteIdsFlow().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            manager.addFavorite(300)
            val afterFirstAdd = awaitItem()
            assertEquals(1, afterFirstAdd.size)
            assertTrue(afterFirstAdd.contains("300"))

            manager.addFavorite(301)
            val afterSecondAdd = awaitItem()
            assertEquals(2, afterSecondAdd.size)

            manager.removeFavorite(300)
            val afterRemove = awaitItem()
            assertEquals(1, afterRemove.size)
            assertTrue(afterRemove.contains("301"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFavoriteCountFlowEmitsCorrectCount() = runTest {
        manager.getFavoriteCountFlow().test {

            var count = awaitItem()
            assertEquals(0, count)

            manager.addFavorite(10)
            count = awaitItem()
            assertEquals(1, count)

            manager.addFavorite(20)
            count = awaitItem()
            assertEquals(2, count)

            manager.addFavorite(30)
            count = awaitItem()
            assertEquals(3, count)

            manager.removeFavorite(20)
            count = awaitItem()
            assertEquals(2, count)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun isFavoriteFlowEmitsCorrectValues() = runTest {
        val recipeId = 500

        manager.isFavoriteFlow(recipeId).test {
            val initial = awaitItem()
            assertFalse(initial)

            manager.addFavorite(recipeId)
            val afterAdd = awaitItem()
            assertTrue(afterAdd)

            manager.removeFavorite(recipeId)
            val afterRemove = awaitItem()
            assertFalse(afterRemove)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addFavoriteWithDuplicateIdDoesNotDuplicate() = runTest {
        val recipeId = 999

        manager.addFavorite(recipeId)
        manager.addFavorite(recipeId)

        val favorites = manager.getAllFavorites()
        assertEquals(1, favorites.size)
        assertTrue(favorites.contains(recipeId.toString()))
    }

    @Test
    fun removeNonExistentFavoriteDoesNothing() = runTest {
        val recipeId = 777
        val initialFavorites = manager.getAllFavorites()

        manager.removeFavorite(recipeId)

        val afterRemoval = manager.getAllFavorites()
        assertEquals(initialFavorites.size, afterRemoval.size)
        assertFalse(manager.isFavorite(recipeId))
    }

    @Test
    fun getAllFavoritesReturnsCurrentState() = runTest {
        val recipeIds = setOf(111, 222, 333)

        recipeIds.forEach { manager.addFavorite(it) }
        val favorites = manager.getAllFavorites()

        assertEquals(3, favorites.size)
        recipeIds.forEach { id ->
            assertTrue(favorites.contains(id.toString()))
        }
    }
}