package com.example.mystreaming.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update

/**
 * DAO da tabela "reviews".
 *
 * Todos os metodos sao `suspend fun`: a chamada tem que acontecer dentro de
 * uma corrotina em Dispatchers.IO para nunca travar a thread principal.
 */
@Dao
interface ReviewsDao {

    @Insert
    suspend fun inserir(review: Review): Long

    @Query("SELECT * FROM reviews ORDER BY id")
    suspend fun buscarTodos(): List<Review>

    /** Consulta filtrada por campo: busca uma avaliacao pelo id (Tela de Comentario Detalhado). */
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun buscarPorId(id: Int): Review?

    /** Filtro por filme: todas as avaliacoes de um filme especifico. */
    @Query("SELECT * FROM reviews WHERE filmeId = :filmeId ORDER BY id")
    suspend fun buscarPorFilme(filmeId: Int): List<Review>

    @Update
    suspend fun atualizar(review: Review)

    @Delete
    suspend fun deletar(review: Review)
}
