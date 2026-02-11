package com.yourcompany.recipecomposeapp.di.factories

interface Factory<T> {
    fun create(): T
}