package com.example.mystreaming.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Entidade "filmes" do banco.
 *
 * Reaproveita a data class do Trabalho 2 (id, title, year, rating) e acrescenta
 * a coluna `naMinhaLista`, que permite tratar a "Sua lista" como uma consulta
 * filtrada sobre a mesma tabela (sem precisar de uma 3a entidade).
 */
@Entity(tableName = "filmes")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val year: String,
    val rating: String,
    val naMinhaLista: Boolean = false
)
