package com.example.mystreaming.data

/**
 * Dados de exemplo do Trabalho 2, agora usados apenas como seed do banco.
 *
 * Os ids sao gerados pelo proprio Room (autoGenerate = true), entao ao inserir
 * em uma tabela vazia os ids saem 1, 2, 3, 4... nessa ordem — e os `filmeId`
 * das avaliacoes continuam apontando para os filmes corretos.
 */
val filmesDeExemplo = listOf(
    MediaItem(title = "Jornada pro Oeste", year = "2012", rating = "6.7"),
    MediaItem(title = "A mulher na lua", year = "1997", rating = "7.7"),
    MediaItem(title = "Filme 3", year = "2023", rating = "8.0"),
    MediaItem(title = "Filme 4", year = "2024", rating = "9.1")
)

val reviewsDeExemplo = listOf(
    Review(
        filmeId = 1,
        usuario = "Joséfa",
        comentario = "As historias da cultura chinesa são muito loucas... Otimo filme",
        nota = 5
    ),
    Review(
        filmeId = 1,
        usuario = "Irineu",
        comentario = "Quem era o vilão no final...? Vc não sabe, nem eu",
        nota = 4
    ),
    Review(
        filmeId = 2,
        usuario = "Aristides",
        comentario = "Esse filme nem existe. Incrivel 10/10",
        nota = 5
    )
)

/** Usuario do perfil do app — usado como autor das avaliacoes. */
const val USUARIO_LOGIN = "Andrey Martins"
