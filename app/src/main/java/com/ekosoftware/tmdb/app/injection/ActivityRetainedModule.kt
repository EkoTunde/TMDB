package com.ekosoftware.tmdb.app.injection

import com.ekosoftware.tmdb.domain.MoviesRepository
import com.ekosoftware.tmdb.domain.MoviesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedModule {

  @Binds
  abstract fun bindMovieRepository(
    moviesRepositoryImpl: MoviesRepositoryImpl
  ): MoviesRepository
}