package com.teddy.teddyresume.data.di

import com.teddy.teddyresume.data.source.GlobalProperty
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SourceModule {

    @Provides
    @Singleton
    fun provideGlobalProperty() : GlobalProperty = GlobalProperty("new Android world!")
}