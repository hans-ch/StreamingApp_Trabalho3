package com.example.mystreaming.data.repository

import com.example.mystreaming.data.local.Review
import com.example.mystreaming.data.local.ReviewsDao
import kotlinx.coroutines.flow.Flow

/**
 * Unica porta de entrada para a tabela "reviews".
 *
 * Quem fala com o DAO e apenas o Repository; quem fala com o Repository e
 * apenas o ViewModel.
 */
class ReviewsRepository(private val dao: ReviewsDao) {

    /** Lista reativa de todas as avaliacoes do banco. */
    val reviews: Flow<List<Review>> = dao.buscarTodos()

    /** Lista reativa das avaliacoes de um filme especifico. */
    fun buscarPorFilme(filmeId: Int): Flow<List<Review>> = dao.buscarPorFilme(filmeId)

    suspend fun buscarPorId(id: Int): Review? = dao.buscarPorId(id)

    suspend fun inserir(review: Review): Long = dao.inserir(review)

    suspend fun atualizar(review: Review) = dao.atualizar(review)

    suspend fun deletar(review: Review) = dao.deletar(review)
}
