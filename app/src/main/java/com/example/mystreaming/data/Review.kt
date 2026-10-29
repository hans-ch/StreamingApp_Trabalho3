package com.example.mystreaming.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * Entidade "reviews" do banco.
 *
 * Reaproveita a data class Review do Trabalho 2 (que vivia dentro da HomeScreen).
 * O `filmeId` e uma chave estrangeira para `filmes.id`: ao apagar um filme,
 * as avaliacoes dele tambem saem (CASCADE), evitando avaliacoes orfas.
 */
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["id"],
            childColumns = ["filmeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("filmeId")]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filmeId: Int,
    val usuario: String,
    val comentario: String,
    val nota: Int
)
