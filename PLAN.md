# PLAN.md — Persistência de dados com Room (Trabalho 3)

> Projeto: `myStreaming_3` (Android + Jetpack Compose, pacote `com.example.mystreaming`)
> Base: evolução do Trabalho 2 (navegação real com `NavHost`, listas em memória)
> Alvo: substituir a persistência em memória (`MutableStateFlow` sem banco) por Room.

**Decisões já tomadas:**
1. **Room 3** — `androidx.room3:room3-runtime:3.0.3` + `ksp("androidx.room3:room3-compiler:3.0.3")`
   (já configurados no projeto e com build verde). Pacote dos imports: `androidx.room3.*`.
2. **Sim** para diálogos de adicionar/editar/remover nas telas existentes (sem telas novas).
3. Este `PLAN.md` também fica na raiz do repositório, junto do `Requisitos.txt` e do `README.md`.

---

## ✅ Status da implementação (concluído)

**Implementado e validado em emulador (Pixel 9 Pro, API 36):**

| Passo | Status |
|---|---|
| 0. Validar Room com 1 Entity + 1 DAO | ✅ `kspDebugKotlin` gera `AppDatabase_Impl`, `FilmesDao_Impl`, `ReviewsDao_Impl` — Room 3 funciona com KSP 2.3.4 (sem necessidade do Plano B) |
| 1. `data/` (6 arquivos) | ✅ Entities, DAOs, `AppDatabase` Singleton e seed |
| 2. `MainViewModel` → `AndroidViewModel` | ✅ `StateFlow`s alimentados pelo banco |
| 3. Rotas com argumento | ✅ `descricao/{filmeId}`, `avaliacao/{filmeId}`, `comentarios/{filmeId}`, `comentario_detalhes/{reviewId}` |
| 4. Home + Minha Lista | ✅ listas do banco + diálogo de adicionar + ✕ |
| 5. Detalhes + Comentários + Comentário | ✅ busca por id, editar, remover |
| 6. Avaliar grava no banco | ✅ `@Insert` com o `filmeId` da rota |
| 7. Limpeza (`MediaItem` movido p/ `data/`) | ✅ imports ajustados; `ProfileScreen` mostra total da lista |
| 8. Build | ✅ `assembleDebug`, `assembleRelease` e `testDebugUnitTest` verdes |
| 9. README (seção 4) | ✅ reescrito com prints em `docs/` |
| 10. Teste de aceitação | ✅ **validado no emulador**: adicionar → `force-stop` → reabrir → item, título editado e comentário continuam lá |

**Arquivos criados:** `data/{MediaItem,Review,FilmesDao,ReviewsDao,AppDatabase,DadosDeExemplo}.kt`,
`PreviewUtils.kt`, `docs/*.png` (6 prints).
**Arquivos alterados:** `MainViewModel.kt`, `AppNavigation.kt`, `Rotas.kt`, `HomeScreen.kt`,
`Description.kt`, `MinhaListaGridScreen.kt`, `comentariosScreen.kt`, `ComentarioDetalhesScreen.kt`,
`ReviewScreen.kt`, `ProfileScreen.kt`, `README.md`.
**Removido:** `MediaItem.kt` (raiz) — a classe passou para `data/`.

---

## 1. Objetivo e escopo

O Trabalho 3 **não pede telas novas nem funcionalidades novas na interface**. Pede que tudo que já
existe (listas, adicionar, remover, editar) passe a ler e gravar no **Room** de verdade.

**Entra:**
- Dependências do Room + plugin KSP
- 2 `@Entity` (`filmes`, `reviews`) reaproveitando as data classes do Trabalho 2 (`MediaItem`, `Review`)
- 2 `@Dao` com os 4 métodos básicos (`@Insert`, `@Query`, `@Update`, `@Delete`) + consultas filtradas
- `AppDatabase` em padrão Singleton
- CRUD completo e persistente nas telas existentes
- Tela de Detalhes buscando o item **por id** direto do banco
- `README.md` com a documentação da seção 4 do `Requisitos.txt`

**Não entra** (conforme seção 5 do `Requisitos.txt`): login, servidor remoto, API, sincronização
na nuvem, arquitetura em camadas formal, telas novas.

---

## 2. Diagnóstico do estado atual (arquitetura existente)

| Arquivo | Estado atual | O que precisa mudar |
|---|---|---|
| `MainActivity.kt` | `setContent { AppScreen() }` | nada |
| `AppScreen.kt` | só chama `AppNavigation()` | nada |
| `AppNavigation.kt` | `NavHost` + barra inferior; passa o **objeto inteiro** `filmeAtual` para a tela de Detalhes; rotas literais (`"perfil"`, `"avaliacao"`) fora do objeto `Rotas` | rotas passam a receber **ids** (`descricao/{filmeId}`, `avaliacao/{filmeId}`, `comentario_detalhes/{reviewId}`); telas carregam do banco pelo id |
| `MainViewModel.kt` | `val todosOsFilmes = sampleMediaList` (fixo) e `_minhaLista: MutableStateFlow` (só memória) | vira `AndroidViewModel` que fala com o `AppDatabase`; a lista vira reflexo do banco |
| `MediaItem.kt` | `data class MediaItem(id, title, year, rating)` em `com.example.mystreaming` | vira `@Entity(tableName = "filmes")` com `@PrimaryKey(autoGenerate = true) val id: Int = 0` (+ flag `naMinhaLista`) |
| `HomeScreen.kt` | declara `data class Review(...)` e `sampleReviews`; renderiza `sampleMediaList` fixo nas seções "Recomendações"/"Novidades" | `Review` vira `@Entity(tableName = "reviews")` (mover p/ `data/`); seções passam a ler do banco; botão ＋ para adicionar filme |
| `Description.kt` (DetalhesScreen) | recebe `filme: MediaItem` pronto; botão "+ lista" só mexe no `StateFlow`; `listaComentariosExemplo` fixo | recebe `filmeId` e busca no banco; "+ lista" grava no banco; editar; remover; comentários do banco |
| `MinhaListaGridScreen.kt` | recebe `minhaLista: List<MediaItem>` do ViewModel | busca `buscarDaMinhaLista()` no banco; ação de remover |
| `comentariosScreen.kt` | filtra `sampleReviews` em memória | busca `buscarPorFilme(filmeId)` no banco; ação de remover comentário |
| `ComentarioDetalhesScreen.kt` | recebe `review: Review` pronto | busca `buscarPorId(reviewId)` no banco; editar comentário |
| `ReviewScreen.kt` (Avaliar) | form com estrelas + comentário; botão "ENVIAR" só faz `popBackStack()` (nada é salvo) | "ENVIAR" grava o `Review` no banco (`@Insert`) |
| `ProfileScreen.kt` | só texto fixo | opcional: mostrar totais vindos do banco |
| `app/build.gradle.kts` | **já tem** `id("com.google.devtools.ksp")` + `room3-runtime`/`room3-compiler` 3.0.3 | manter; opcionalmente mover para o Version Catalog |
| `build.gradle.kts` | **já tem** `id("com.google.devtools.ksp") version "2.3.4" apply false` | manter (KSP já sincronizado antes do `ksp(...)`, como exige o §3.1) |

**Estado do build agora:** `.\gradlew.bat :app:compileDebugKotlin` → **BUILD SUCCESSFUL** (configuração
atual com `androidx.room3:room3-runtime:3.0.3` + KSP 2.3.4 já resolve e compila).

### 2.1 O que foi verificado na API do Room 3 (para não haver surpresa na implementação)

Inspeção dos artefatos `androidx.room3:room3-runtime-android:3.0.3` e `androidx.room3:room3-common-jvm:3.0.3`:

- Anotações: `androidx.room3.Entity`, `.PrimaryKey`, `.Dao`, `.Insert`, `.Query`, `.Update`, `.Delete`,
  `.Database`, `.ForeignKey`, `.Index` — **todas com os mesmos nomes e parâmetros do Room 2**
  (`Entity.tableName`, `PrimaryKey.autoGenerate`, `ForeignKey.CASCADE`, `Database.entities/version/exportSchema`).
- `Room.databaseBuilder(context, klass, name)` → devolve `RoomDatabase.Builder<T>` → `.build(): T`
  — **exatamente o mesmo formato** do exemplo de "Filmes" corrigido em aula.
- `RoomDatabase.Builder` tem `fallbackToDestructiveMigration()`.
- `RoomDatabase` continua sendo `abstract class` a ser estendida.

Ou seja: **todo o código do plano abaixo é idêntico ao do Room 2, só muda o pacote do import**
(`androidx.room3.*` em vez de `androidx.room.*`).

### 2.2 Observação de conformidade com o `Requisitos.txt` (para o README)

O §3.1 do enunciado cita `room-runtime`, `room-ktx` e `ksp("androidx.room:room-compiler:$room_version")`.
No **Room 3** o artefato `room3-ktx` **não existe**: o conteúdo do `-ktx` (extensões de corrotina/Flow)
foi fundido no `room3-runtime`, e o Room 3 passa a ser multi-plataforma (`room3-common`,
`room3-runtime-android`, etc.). Funcionalmente o requisito é atendido pelo `room3-runtime`.

**Justificativa para documentar no README:** mantemos o Room 3 porque (a) já está configurado e com
build verde neste projeto, (b) é a versão atual do Room (a 2.x está em manutenção), (c) a API usada é
idêntica à do exemplo visto em aula — só o pacote muda (`androidx.room3.*`).
**Plano B (se o professor exigir o `androidx.room` 2.x):** trocar as dependências por
`androidx.room:room-runtime:2.8.5` + `androidx.room:room-ktx:2.8.5` +
`ksp("androidx.room:room-compiler:2.8.5")` e trocar **todos** os imports de `androidx.room3.*` para
`androidx.room.*`. Nenhuma outra linha do código muda (mesma API) — por isso o plano foi escrito de
forma independente dessa escolha.

---

## 3. Dependências do Room (Gradle)

### 3.1 Estado atual (já pronto, na ordem exigida pelo §3.1 do enunciado)

1. **Primeiro** o plugin KSP é sincronizado na raiz — `build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.android.application) apply false
       alias(libs.plugins.compose.compiler) apply false
       id("com.google.devtools.ksp") version "2.3.4" apply false   // <- já feito
   }
   ```
2. **Depois** o plugin é aplicado no módulo e o `ksp(...)` é declarado — `app/build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.android.application)
       alias(libs.plugins.compose.compiler)
       id("com.google.devtools.ksp")                                  // <- já feito
   }

   dependencies {
       // ... dependências existentes ...
       val room_version = "3.0.3"
       implementation("androidx.room3:room3-runtime:$room_version")
       ksp("androidx.room3:room3-compiler:$room_version")
   }
   ```

### 3.2 Opcional: Version Catalog (estilo do projeto)

`gradle/libs.versions.toml` já é usado no projeto (`libs.androidx.core.ktx`, etc.). Deixar o Room
também no catálogo deixa o build mais limpo:

```toml
[versions]
room = "3.0.3"

[libraries]
androidx-room-runtime = { group = "androidx.room3", name = "room3-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room3", name = "room3-compiler", version.ref = "room" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version = "2.3.4" }
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}
dependencies {
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
```

> Se a migração para o catálogo der qualquer dor de resolução, manter as strings literais
> (funciona igual e é o que o enunciado mostra).

---

## 4. Modelo de dados (Entities)

Novo pacote: `app/src/main/java/com/example/mystreaming/data/`

### 4.1 `data/MediaItem.kt` → tabela `filmes`

```kotlin
package com.example.mystreaming.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "filmes")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val year: String,
    val rating: String,
    val naMinhaLista: Boolean = false   // true = o usuário salvou o filme na "Sua lista"
)
```

**Por que estes campos**
- `id`: `Int` com `autoGenerate = true` e default `0` (exatamente como pede o §3.1). O `0` faz o SQLite
  auto-incrementar; no *seed* inserimos com `id = 0` também, e o banco devolve 1, 2, 3, 4...
- `title`, `year`, `rating`: os mesmos campos da data class do Trabalho 2 — nada muda na UI (`rating`
  continua `String` para não quebrar `"★ ${item.rating}"`).
- `naMinhaLista`: única coluna nova. Resolve o problema de "Minha Lista" sem criar uma 3ª tabela:
  a lista do usuário é uma **consulta filtrada** (`WHERE naMinhaLista = 1`) sobre a mesma tabela.
  Sem ela, seria preciso uma tabela de junção + mais uma Entity/DAO (fora do escopo do trabalho).

### 4.2 `data/Review.kt` → tabela `reviews`

```kotlin
package com.example.mystreaming.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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
    val filmeId: Int,       // FK -> filmes.id
    val usuario: String,
    val comentario: String,
    val nota: Int
)
```

**Por que estes campos**
- Reaproveita a `data class Review` do Trabalho 2 (que hoje mora dentro de `HomeScreen.kt`) — 2ª Entity
  obrigatória, com campos idênticos aos que a UI já usa.
- `filmeId` vira **chave estrangeira** com `CASCADE`: apagar um filme apaga as avaliações dele
  (evita review órfão apontando para um id que não existe).
- `nota: Int` continua 1..5 (estrelas da tela "Avaliar").
- `usuario: String` — hoje fixo "Andrey Martins" (mesmo do `ProfileScreen`); com o banco, cada avaliação
  gravada guarda o autor no momento em que foi criada.

### 4.3 Seed dos dados de exemplo

O app precisa abrir com conteúdo (seções "Recomendações"/"Novidades" e a lista de comentários).
O seed é feito **pelos próprios DAOs** (sem SQL manual), só na primeira vez:

```kotlin
// data/DadosDeExemplo.kt
package com.example.mystreaming.data

val filmesDeExemplo = listOf(
    MediaItem(title = "Jornada pro Oeste", year = "2012", rating = "6.7"),
    MediaItem(title = "A mulher na lua",    year = "1997", rating = "7.7"),
    MediaItem(title = "Filme 3",            year = "2023", rating = "8.0"),
    MediaItem(title = "Filme 4",            year = "2024", rating = "9.1")
)

val reviewsDeExemplo = listOf(
    Review(filmeId = 1, usuario = "Joséfa",
        comentario = "As historias da cultura chinesa são muito loucas... Otimo filme", nota = 5),
    Review(filmeId = 1, usuario = "Irineu",
        comentario = "Quem era o vilão no final...? Vc não sabe, nem eu", nota = 4),
    Review(filmeId = 2, usuario = "Aristides",
        comentario = "Esse filme nem existe. Incrivel 10/10", nota = 5)
)
```

Inserindo com `id = 0` (autoGenerate) em tabela vazia, os ids saem 1, 2, 3, 4 → os `filmeId` do seed
(1, 1, 2) continuam batendo com os ids gerados, como no Trabalho 2.

---

## 5. DAOs

### 5.1 `data/FilmesDao.kt`

```kotlin
package com.example.mystreaming.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update

@Dao
interface FilmesDao {

    @Insert
    suspend fun inserir(filme: MediaItem): Long

    @Query("SELECT * FROM filmes ORDER BY id")
    suspend fun buscarTodos(): List<MediaItem>

    // consulta filtrada por campo (exigência do §3.1) — usada pela Tela de Detalhes (§3.3)
    @Query("SELECT * FROM filmes WHERE id = :id")
    suspend fun buscarPorId(id: Int): MediaItem?

    // filtro da "Sua lista"
    @Query("SELECT * FROM filmes WHERE naMinhaLista = 1 ORDER BY id")
    suspend fun buscarDaMinhaLista(): List<MediaItem>

    @Update
    suspend fun atualizar(filme: MediaItem)

    @Delete
    suspend fun deletar(filme: MediaItem)
}
```

### 5.2 `data/ReviewsDao.kt`

```kotlin
package com.example.mystreaming.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update

@Dao
interface ReviewsDao {

    @Insert
    suspend fun inserir(review: Review): Long

    @Query("SELECT * FROM reviews ORDER BY id")
    suspend fun buscarTodos(): List<Review>

    // consulta filtrada por campo — usada pela tela de Comentário Detalhado
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun buscarPorId(id: Int): Review?

    // filtro por filme (relacionamento com a tabela filmes)
    @Query("SELECT * FROM reviews WHERE filmeId = :filmeId ORDER BY id")
    suspend fun buscarPorFilme(filmeId: Int): List<Review>

    @Update
    suspend fun atualizar(review: Review)

    @Delete
    suspend fun deletar(review: Review)
}
```

### 5.3 Decisões sobre as queries (para a documentação do README)

- Os 4 métodos básicos existem nos **dois** DAOs, todos `suspend fun`.
- `buscarPorId` (ambos) e `buscarPorFilme` (reviews) são as **consultas filtradas** — atendem ao §3.1
  e alimentam a Tela de Detalhes (§3.3).
- `buscarDaMinhaLista` resolve a "Sua lista" sem criar uma 3ª entidade.
- `ORDER BY id` em todas as leituras para a ordem da lista ser **estável** (o SQLite não garante ordem
  sem `ORDER BY`).
- `buscarPorId` retorna `MediaItem?`/`Review?` (nullable) porque o id pode não existir mais
  (filme removido) — o fluxo trata o `null` mostrando "não encontrado".

---

## 6. `data/AppDatabase.kt` — Singleton

```kotlin
package com.example.mystreaming.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [MediaItem::class, Review::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filmesDao(): FilmesDao
    abstract fun reviewsDao(): ReviewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mystreaming.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

Padrão Singleton com `companion object` + `@Volatile` + `synchronized` (exatamente como o §3.1 pede).
`version = 1` (primeira versão, sem migração). `fallbackToDestructiveMigration()` é **opcional** e só
faria sentido se subíssemos a versão depois — na v1 não é necessário.

---

## 7. Camada de acesso: `MainViewModel` + padrão de corrotina

### 7.1 `MainViewModel` como fonte única de verdade

`MainViewModel` vira `AndroidViewModel` (precisa do `Context` para o `Room.databaseBuilder`), mantém os
`StateFlow`s que já existem (a UI atual já coleta `minhaLista` com `collectAsState()`) e passa a popular
esses fluxos **a partir do banco**.

```kotlin
package com.example.mystreaming

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mystreaming.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    private val filmesDao = db.filmesDao()
    private val reviewsDao = db.reviewsDao()

    private val _filmes = MutableStateFlow<List<MediaItem>>(emptyList())
    val filmes: StateFlow<List<MediaItem>> = _filmes.asStateFlow()

    private val _minhaLista = MutableStateFlow<List<MediaItem>>(emptyList())
    val minhaLista: StateFlow<List<MediaItem>> = _minhaLista.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    init { popularBancoInicial() }   // seed só se as tabelas estiverem vazias

    // ---------- Leituras (chamadas de dentro de CoroutineScope(Dispatchers.IO).launch { }) ----------
    suspend fun carregarFilmes() { _filmes.value = filmesDao.buscarTodos() }
    suspend fun carregarMinhaLista() { _minhaLista.value = filmesDao.buscarDaMinhaLista() }
    suspend fun carregarReviews(filmeId: Int) { _reviews.value = reviewsDao.buscarPorFilme(filmeId) }
    suspend fun buscarFilmePorId(id: Int): MediaItem? = filmesDao.buscarPorId(id)
    suspend fun buscarReviewPorId(id: Int): Review? = reviewsDao.buscarPorId(id)

    // ---------- Escritas ----------
    suspend fun inserirFilme(filme: MediaItem) {
        filmesDao.inserir(filme)
        carregarFilmes(); carregarMinhaLista()
    }

    suspend fun atualizarFilme(filme: MediaItem) {
        filmesDao.atualizar(filme)
        carregarFilmes(); carregarMinhaLista()
    }

    suspend fun deletarFilme(filme: MediaItem) {
        filmesDao.deletar(filme)
        carregarFilmes(); carregarMinhaLista()
    }

    suspend fun alternarMinhaLista(filme: MediaItem) {
        filmesDao.atualizar(filme.copy(naMinhaLista = !filme.naMinhaLista))
        carregarFilmes(); carregarMinhaLista()
    }

    suspend fun inserirReview(review: Review) {
        reviewsDao.inserir(review)
        carregarReviews(review.filmeId)
    }

    suspend fun atualizarReview(review: Review) {
        reviewsDao.atualizar(review)
        carregarReviews(review.filmeId)
    }

    suspend fun deletarReview(review: Review) {
        reviewsDao.deletar(review)
        carregarReviews(review.filmeId)
    }

    private suspend fun popularBancoInicial() {
        if (filmesDao.buscarTodos().isEmpty()) {
            filmesDeExemplo.forEach { filmesDao.inserir(it) }
            reviewsDeExemplo.forEach { reviewsDao.inserir(it) }
        }
    }
}
```

Observações:
- O `StateFlow` continua sendo a "ponte" entre banco e UI: a escrita vai pro banco e o fluxo é
  recarregado, então a tela reflete o novo estado **sem** `mutableStateListOf` manual.
- `viewModel()` (Compose) resolve `AndroidViewModel` automaticamente em uma `ComponentActivity`.
- `popularBancoInicial()` roda no `init`; pode ser chamada de um `viewModelScope.launch(Dispatchers.IO)`
  para não travar a criação do ViewModel.

### 7.2 Padrão obrigatório nas telas (§3.1 / §3.2 / §3.4 do enunciado)

Toda leitura de lista usa `LaunchedEffect` + `CoroutineScope(Dispatchers.IO).launch` + `try/catch` + `Log.e`:

```kotlin
// dentro de uma @Composable
val scope = rememberCoroutineScope()

LaunchedEffect(Unit) {
    scope.launch(Dispatchers.IO) {
        try {
            mainViewModel.carregarFilmes()      // -> dao.buscarTodos() dentro de Dispatchers.IO
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao carregar filmes do banco", e)
        }
    }
}
```

E toda escrita:

```kotlin
Button(onClick = {
    scope.launch(Dispatchers.IO) {
        try {
            mainViewModel.inserirFilme(novoFilme)     // -> dao.inserir(...)
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao salvar filme", e)
        }
    }
})
```

Nas telas de Detalhe (que precisam do valor devolvido), a volta para a main thread é explícita:

```kotlin
var filme by remember { mutableStateOf<MediaItem?>(null) }

LaunchedEffect(filmeId) {                       // <- §3.3: busca pelo id vindo da navegação
    scope.launch(Dispatchers.IO) {
        try {
            val f = mainViewModel.buscarFilmePorId(filmeId)
            withContext(Dispatchers.Main) { filme = f }
        } catch (e: Exception) {
            Log.e("DetalhesScreen", "Erro ao buscar filme $filmeId", e)
        }
    }
}
```

---

## 8. Rotas e navegação

`Rotas.kt` passa a carregar as rotas com argumento:

```kotlin
object Rotas {
    const val HOME     = "home"
    const val DESCRICAO = "descricao/{filmeId}"
    const val AVALIACAO = "avaliacao/{filmeId}"
    const val MINHA_LISTA_GRID = "grid"
    const val COMENTARIOS = "comentarios/{filmeId}"
    const val COMENTARIO_DETALHES = "comentario_detalhes/{reviewId}"
    const val PERFIL    = "perfil"
}
```

Navegação: `navController.navigate("descricao/${filme.id}")` e leitura do argumento na composição:

```kotlin
composable(route = Rotas.DESCRICAO) { backStackEntry ->
    val filmeId = backStackEntry.arguments?.getString("filmeId")?.toIntOrNull() ?: 0
    DetalhesScreen(navController = navController, filmeId = filmeId, ...)
}
```

Ganho direto: **nenhum objeto passa mais pela navegação** — só o `id`. É isso que permite à Tela de
Detalhes buscar o item direto do banco (§3.3) e ainda sobreviver ao *process death*.

---

## 9. Mudanças por tela

| # | Tela / arquivo | Mudança | DAO envolvido |
|---|---|---|---|
| 1 | `data/MediaItem.kt` (novo), `data/Review.kt` (novo) | Entities | — |
| 2 | `data/FilmesDao.kt`, `data/ReviewsDao.kt` (novos) | DAOs | — |
| 3 | `data/AppDatabase.kt` (novo) | Singleton | — |
| 4 | `data/DadosDeExemplo.kt` (novo) | Seed | `inserir` |
| 5 | `MediaItem.kt` (raiz) | **remover** (movido para `data/`) — ajustar imports em 6 arquivos | — |
| 6 | `MainViewModel.kt` | `AndroidViewModel` + DAOs + `StateFlow`s + funcs `suspend` | todos |
| 7 | `AppNavigation.kt` | rotas com argumento, `Rotas` em vez de literais, `viewModel()` compartilhado | — |
| 8 | `HomeScreen.kt` | remover `Review`/`sampleReviews` dali; seções "Recomendações"/"Novidades" lêem `filmes` do banco; "Sua lista" lê `minhaLista`; **botão ＋ na TopAppBar abre diálogo "Adicionar filme"** (3 campos: título, ano, nota) → `inserir` | `buscarTodos`, `buscarDaMinhaLista`, `inserir` |
| 9 | `Description.kt` (DetalhesScreen) | recebe `filmeId`; busca por id (§3.3); botão "+ lista" → `alternarMinhaLista` (UPDATE); **ícone ✏️ abre diálogo de edição** (título/ano/nota) → `atualizar`; **ícone 🗑 (com confirmação) apaga o filme** → `deletar`; "Avaliar" navega `avaliacao/{filmeId}`; bloco "Comentários" lista as reviews do banco | `buscarPorId`, `atualizar`, `deletar`, `buscarPorFilme` |
| 10 | `MinhaListaGridScreen.kt` | lê `minhaLista` do banco; cada card ganha **✕ para remover da lista** (UPDATE da flag) e o clique abre o detalhe pelo id | `buscarDaMinhaLista`, `atualizar` |
| 11 | `comentariosScreen.kt` | lê reviews do banco por `filmeId`; card com **🗑 para apagar o comentário** (DELETE) | `buscarPorFilme`, `deletar` |
| 12 | `ComentarioDetalhesScreen.kt` | busca o review **por id** no banco; **✏️ edita** texto/nota → `atualizar`; **🗑 apaga** → `deletar` | `buscarPorId`, `atualizar`, `deletar` |
| 13 | `ReviewScreen.kt` (Avaliar) | recebe `filmeId` da rota; título do filme carregado do banco; "ENVIAR" grava a `Review` (estrelas + comentário + usuário do perfil) → `inserir`; depois `popBackStack()` | `inserir`, `buscarPorId` |
| 14 | `ProfileScreen.kt` | **opcional**: mostrar "X filmes na sua lista / Y avaliações" vindo do banco | `buscarTodos` |
| 15 | `README.md` | documentação da seção 4 (ver seção 12) | — |

### 9.1 Onde cada método do DAO é exercitado pela UI (roteiro da apresentação)

| Operação | DAO | Caminho na UI |
|---|---|---|
| **Inserir** | `filmesDao.inserir` | Home → ＋ na TopAppBar → diálogo "Adicionar filme" → Salvar |
| **Inserir** | `reviewsDao.inserir` | Detalhes → "Avaliar" → estrelas + comentário → ENVIAR |
| **Atualizar** | `filmesDao.atualizar` | Detalhes → ✏️ → editar título/ano/nota → Salvar |
| **Atualizar** | `filmesDao.atualizar` (flag) | Detalhes → "+ lista" / "− lista"; Minha Lista → ✕ |
| **Atualizar** | `reviewsDao.atualizar` | Comentário Detalhado → ✏️ → editar texto/nota |
| **Remover** | `reviewsDao.deletar` | Comentários → 🗑 no card / Comentário Detalhado → 🗑 |
| **Remover** | `filmesDao.deletar` | Detalhes → 🗑 (com diálogo de confirmação) |
| **Buscar (lista)** | `filmesDao.buscarTodos` | Home — `LaunchedEffect(Unit)` ao abrir |
| **Buscar (filtro)** | `filmesDao.buscarDaMinhaLista` | Home "Sua lista" / Tela "Minha Lista" |
| **Buscar (por id)** | `filmesDao.buscarPorId` | **Tela de Detalhes (§3.3)** |
| **Buscar (por id)** | `reviewsDao.buscarPorId` | Tela de Comentário Detalhado |
| **Buscar (por campo)** | `reviewsDao.buscarPorFilme` | Tela de Comentários (reviews de um filme) |

> Observação: o requisito fala em "adicionar/remover item". Aqui **"remover da Minha Lista"** é um
> `@Update` (flag `naMinhaLista = false`) e **"remover o filme do app"** é o `@Delete` — os dois `@Delete`
> (filmes e reviews) têm caminho real na UI, e o `atualizar` também. Todos os 8 métodos são exercitados.

### 9.2 Desenho dos diálogos (sem criar telas novas)

- **Diálogo "Adicionar filme"** (`AlertDialog` na `HomeScreen`, disparado pelo ＋ da TopAppBar):
  3 `OutlinedTextField` (título, ano, nota) + botões Cancelar / Salvar. Ao salvar: `inserirFilme(...)`.
- **Diálogo "Editar filme"** (`AlertDialog` na `DetalhesScreen`, disparado pelo ✏️ da TopAppBar):
  mesmos 3 campos, preenchidos com os valores atuais. Ao salvar: `atualizarFilme(filme.copy(...))`.
- **Diálogo "Editar comentário"** (`AlertDialog` na `ComentarioDetalhesScreen`, ✏️):
  `TextField` do texto + seletor de estrelas. Ao salvar: `atualizarReview(review.copy(...))`.
- **Confirmação de exclusão** (`AlertDialog` simples com "Cancelar"/"Excluir") antes de qualquer 🗑.
- Todos os diálogos usam `Material3` (`AlertDialog`, `OutlinedTextField`, `TextButton`), sem dependências novas.

---

## 10. Passo a passo de implementação (ordem sugerida)

**Passo 0 — Validar o Room antes de escrever as telas** (10 min, evita retrabalho):
1. Criar `data/MediaItem.kt` (Entity), `data/FilmesDao.kt` (DAO) e `data/AppDatabase.kt` (Singleton).
2. `.\gradlew.bat :app:assembleDebug` → precisa passar (o `kspDebugKotlin` tem que gerar o
   `AppDatabase_Impl`). Se der erro de KSP/Room → aplicar o **Plano B** da seção 2.2
   (`androidx.room` 2.8.5 + trocar os imports), que não muda mais nada no plano.

**Passo 1 — Camada de dados (6 arquivos novos em `data/`)**
`MediaItem.kt`, `Review.kt`, `FilmesDao.kt`, `ReviewsDao.kt`, `AppDatabase.kt`, `DadosDeExemplo.kt`.

**Passo 2 — ViewModel**
`MainViewModel` → `AndroidViewModel`, com `StateFlow`s + funções `suspend` (§7.1) + seed.
Remover `sampleMediaList`/`sampleReviews` do ViewModel (mover para `data/DadosDeExemplo.kt`).

**Passo 3 — Navegação**
`Rotas.kt` com argumentos; `AppNavigation.kt` lendo `getString("filmeId")`/`getString("reviewId")`.

**Passo 4 — Telas de lista**
`HomeScreen.kt` (seções lendo do banco + diálogo de adicionar) e `MinhaListaGridScreen.kt`.

**Passo 5 — Telas de detalhe**
`Description.kt` (busca por id + editar + remover), `comentariosScreen.kt`,
`ComentarioDetalhesScreen.kt`.

**Passo 6 — Formulário de avaliação**
`ReviewScreen.kt` gravando a review no banco.

**Passo 7 — Limpeza e polimento**
Remover `MediaItem.kt` da raiz e ajustar imports (`HomeScreen`, `Description`, `MinhaListaGridScreen`,
`AppNavigation`, `MainViewModel`). `ProfileScreen.kt` com as estatísticas do banco (opcional).

**Passo 8 — Build + testes**
`.\gradlew.bat :app:assembleDebug` (e `.\gradlew.bat :app:assembleRelease` opcional).

**Passo 9 — Documentação** (seção 12).

**Passo 10 — Vídeo/prints do teste de aceitação** (seção 11).

---

## 11. Teste de aceitação (o que será mostrado na apresentação)

Roteiro do vídeo (curto, ~1–2 min):

1. Abrir o app → Home mostra "Recomendações"/"Novidades" vindas do banco.
2. Tocar no ＋ → adicionar um filme novo ("Meu Filme Teste", 2026, 9.0) → ele aparece na lista.
3. Entrar no filme → editar (✏️) o título → salva → o título muda na lista.
4. Entrar em "Sua lista" → o filme está lá → ✕ remove da lista → some.
5. Abrir "Avaliar" → 4 estrelas + comentário → ENVIAR → o comentário aparece na tela de Comentários
   do filme.
6. **Fechar o app completamente** e reabrir:
   ```bash
   adb shell am force-stop com.example.mystreaming   # encerra o processo (some dos recentes)
   adb shell monkey -p com.example.mystreaming 1     # reabre o app
   ```
   → o filme adicionado, o título editado e o comentário salvo **continuam lá**.
7. Remover o item (🗑) → sumir da lista.

⚠️ **Não usar** `adb shell pm clear com.example.mystreaming` na demonstração: isso apaga o banco
(é justamente o oposto do que a gente quer provar). "Fechar o app" = `force-stop` / arrastar pra fora
dos recentes.

---

## 12. Documentação (README.md — seção 4 do `Requisitos.txt`)

O `README.md` atual documenta a **segunda entrega**; precisa virar a documentação do **Trabalho 3**
(ou ganhar uma seção nova no topo). Conteúdo obrigatório:

1. **Como estava no Trabalho 2 e o que mudou** → `MutableStateFlow`/`sampleMediaList` em memória →
   Room; incluir print "antes" (lista que some ao fechar) e "depois".
2. **Quais Entity, DAO e Database foram criados e por que estes campos** → §4 e §5 deste plano
   (tabela com `filmes` e `reviews`, e a justificativa da coluna `naMinhaLista` e da FK `filmeId`).
3. **Como estruturamos as queries do DAO** → os 4 básicos em ambos + `buscarPorId` (filmes e reviews),
   `buscarPorFilme` (reviews), `buscarDaMinhaLista` (filmes) + `ORDER BY id` para ordenação estável.
4. **Dificuldade de migrar de `mutableStateListOf` para Room** → ponto honesto: o `id` que antes era
   manual (`1, 2, 3, 4`) passou a ser gerado pelo banco, e os `filmeId` fixos das reviews deixaram de
   bater; a solução foi semear com `id = 0` (autoGenerate) para o banco gerar 1..4 na mesma ordem.
   Outro ponto: o `StateFlow` continua existindo, mas agora é só o espelho do banco.
   **Incluir também**: por que Room 3 (`androidx.room3`) em vez do `androidx.room` 2.x citado no
   enunciado (seção 2.2 deste plano).
5. **Print/vídeo do teste de persistência** → vídeo do roteiro da seção 11 + prints.
6. **Dificuldades de cada integrante** → preencher com as histórias do trio.
7. **Como rodar o projeto** → abrir no Android Studio (sync do Gradle, `Run`), ou
   `.\gradlew.bat :app:assembleDebug` + instalar o APK; aparelho/emulador com API 29+.

---

## 13. Checklist do `Requisitos.txt` (seção 7) × o plano

| Item do checklist | Onde é resolvido |
|---|---|
| [ ] Dependências do Room configuradas e projeto compila | §3.1 / §10 Passo 0 |
| [ ] ≥ 2 `@Entity` com `@PrimaryKey(autoGenerate = true)` | §4.1 (`filmes`) e §4.2 (`reviews`) |
| [ ] ≥ 2 `@Dao` com `@Insert`, `@Query`, `@Update`, `@Delete` (suspend) | §5.1 e §5.2 |
| [ ] ≥ 1 DAO com consulta filtrada por campo | §5.1 `buscarPorId`, §5.2 `buscarPorId`/`buscarPorFilme` |
| [ ] Tela de Detalhes busca o item do banco pelo id | §9 item 9 + §7.2 (`buscarFilmePorId`) |
| [ ] `AppDatabase` em padrão Singleton | §6 |
| [ ] Listas buscam do banco ao abrir a tela | §9 itens 8 e 10 (`LaunchedEffect(Unit)`) |
| [ ] Adicionar item pela UI salva no banco | §9 itens 8 (diálogo ＋) e 13 (ENVIAR) |
| [ ] Editar (atualizar) item pela UI funciona e persiste | §9 itens 9 e 12 (diálogos de edição) |
| [ ] Remover item pela UI apaga do banco | §9 itens 9, 11 e 12 (🗑) |
| [ ] Testei: adicionar, fechar, reabrir → continua lá | §11 |
| [ ] Toda operação de banco dentro de Coroutine no `Dispatchers.IO` | §7.2 (padrão obrigatório) |
| [ ] `try/catch` + `Log.e` | §7.2 |
| [ ] `LaunchedEffect(Unit)` para buscar ao abrir | §7.2 |
| [ ] Documentação com prints/vídeo no repositório | §12 |
| [ ] Novo repositório GitHub | fora do escopo técnico — lembrar de criar e copiar o código (§6 do `Requisitos.txt`) |

---

## 14. Riscos e pontos de atenção

1. **Room 3 + KSP 2.3.4** — o único risco real de build (o `kspDebugKotlin` nunca rodou com uma
   `@Entity` de verdade neste projeto). Mitigação: **Passo 0** valida antes de escrever as telas;
   Plano B documentado (§2.2) — a API é idêntica, só mudam os imports.
2. **Duplicação no seeding** — se o seed rodar duas vezes, duplica filmes. Solução: só semeia se
   `buscarTodos().isEmpty()`; como a tabela fica populada, o seed nunca repete.
3. **`autoGenerate` + ids fixos** — as reviews do seed referenciam `filmeId` 1..3. Se o usuário apagar o
   filme 1, o `CASCADE` apaga as reviews 1 e 2 (e o id 1 não é reaproveitado) — comportamento esperado.
4. **Apagar o filme x apagar da lista** — são ações diferentes na UI (🗑 apaga tudo; ✕ / "− lista" só
   tira da lista). Manter as duas para o `@Update` e o `@Delete` terem uso real.
5. **Atualização da lista na UI** — toda escrita recarrega o `StateFlow` correspondente; se uma tela
   "esquentar" (não recarregar), conferir se a função `suspend` está chamando o `carregar*` certo.
6. **Imports** — mover `MediaItem` para o pacote `data` exige ajustar imports em `HomeScreen`,
   `Description`, `MinhaListaGridScreen`, `AppNavigation` e `MainViewModel`.
7. **Não quebrar o que já funciona** — `TopAppBar`, `BottomBar`, `LazyColumn`/`LazyVerticalGrid`/`LazyRow`,
   `Card` e os `@Preview` continuam iguais; só a origem dos dados muda.
8. **Fila do `navController`** — `Rotas.COMENTARIOS` passa a exigir `filmeId`; conferir todos os
   `navigate(...)` (inclusive o "Mais comentários" dentro da tela de Detalhes).

---

## 15. Ordem de implementação resumida

```
0. Validar Room com 1 Entity + 1 DAO (build mínimo)  ->  se falhar: Plano B (androidx.room 2.8.5)
1. data/: MediaItem, Review, FilmesDao, ReviewsDao, AppDatabase, DadosDeExemplo
2. MainViewModel: AndroidViewModel + StateFlows + funcs suspend + seed
3. Rotas.kt + AppNavigation.kt: navegação por id
4. HomeScreen + MinhaListaGridScreen: listas do banco + diálogo de adicionar
5. Description (Detalhes) + ComentariosScreen + ComentarioDetalhesScreen: buscar por id, editar, remover
6. ReviewScreen (Avaliar): gravar review no banco
7. Limpeza (mover MediaItem p/ data/, ajustar imports) + ProfileScreen com stats (opcional)
8. assembleDebug verde
9. README (documentação da seção 4)
10. Vídeo/prints do teste de persistência
```
