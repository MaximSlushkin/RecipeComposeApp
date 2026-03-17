package com.yourcompany.recipecomposeapp.di

import android.app.Application

class RecipeApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)

    }
}