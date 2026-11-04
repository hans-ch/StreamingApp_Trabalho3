package com.example.mystreaming.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystreaming.data.repository.FilmesRepository
import com.example.mystreaming.data.repository.ReviewsRepository
import com.example.mystreaming.data.repository.SeedRepository

/**
 * O Android nao sabe criar sozinho um ViewModel que recebe parametros no
 * construtor. A Factory e quem injeta o Repository na hora da criacao.
 *
 * Nas telas o ViewModel e obtido com:
 * `viewModel(factory = FilmesViewModelFactory(repository))`
 */

/** Factory do [FilmesViewModel]. */
class FilmesViewModelFactory(
    private val repository: FilmesRepository,
    private val seedRepository: SeedRepository,
    private val filmeId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmesViewModel::class.java)) {
            return FilmesViewModel(repository, seedRepository, filmeId) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}

/** Factory do [ReviewsViewModel]. */
class ReviewsViewModelFactory(
    private val repository: ReviewsRepository,
    private val filmesRepository: FilmesRepository,
    private val filmeId: Int? = null,
    private val reviewId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
            return ReviewsViewModel(repository, filmesRepository, filmeId, reviewId) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
