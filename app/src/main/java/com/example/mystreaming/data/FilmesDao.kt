package com.example.mystreaming.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update

/**
 * DAO da tabela "filmes".
 *
 * Todos os metodos sao `suspend fun`: a chamada tem que acontecer dentro de
 * uma corrotina em Dispatchers.IO para nunca travar a thread principal.
 */
@Dao
interface FilmesDao {

    @Insert
    suspend fun inserir(filme: MediaItem): Long

    @Query("SELECT * FROM filmes ORDER BY id")
    suspend fun buscarTodos(): List<MediaItem>

    /** Consulta filtrada por campo: busca um filme pelo id (usada na Tela de Detalhes). */
    @Query("SELECT * FROM filmes WHERE id = :id")
    suspend fun buscarPorId(id: Int): MediaItem?

    /** Filtro da "Sua lista": apenas os filmes marcados pelo usuario. */
    @Query("SELECT * FROM filmes WHERE naMinhaLista = 1 ORDER BY id")
    suspend fun buscarDaMinhaLista(): List<MediaItem>

    @Update
    suspend fun atualizar(filme: MediaItem)

    @Delete
    suspend fun deletar(filme: MediaItem)
}
