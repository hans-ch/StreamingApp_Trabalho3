package com.example.mystreaming

import androidx.lifecycle.ViewModel
import com.example.mystreaming.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


//Lista com itens de teste
val sampleMediaList = listOf(
    MediaItem(1, "Jornada pro Oeste", "2012", "6.7"),
    MediaItem(2, "A mulher na lua", "1997", "7.7"),
    MediaItem(3, "Filme 3", "2023", "8.0"),
    MediaItem(4, "Filme 4", "2024", "9.1")
)

class MainViewModel : ViewModel() {

    // Lista com os filmes disponíveis
    val todosOsFilmes: List<MediaItem> = sampleMediaList

    // Estado reativo da "Sua lista" (inicia vazia ou com elementos predefinidos)
    private val _minhaLista = MutableStateFlow<List<MediaItem>>(emptyList())
    val minhaLista: StateFlow<List<MediaItem>> = _minhaLista.asStateFlow()

    // Alterna a presença do filme na lista do utilizador
    fun toggleMinhaLista(filme: MediaItem) {
        val listaAtual = _minhaLista.value.toMutableList()
        if (listaAtual.any { it.id == filme.id }) {
            listaAtual.removeAll { it.id == filme.id }
        } else {
            listaAtual.add(filme)
        }
        _minhaLista.value = listaAtual
    }

    // Verifica se um filme já está na lista
    fun isNaMinhaLista(filmeId: Int): Boolean {
        return _minhaLista.value.any { it.id == filmeId }
    }
}
