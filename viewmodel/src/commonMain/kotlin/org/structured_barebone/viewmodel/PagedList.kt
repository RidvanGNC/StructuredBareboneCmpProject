package org.structured_barebone.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.Result

/** One page of a cursor-paged list; [nextCursor] is null on the last page. */
data class Page<T>(val items: List<T>, val nextCursor: String?)

data class PagedListState<T>(
    val query: String = "",
    val items: List<T> = emptyList(),
    /** The first page (or a refresh) is loading. */
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: AppError? = null,
)

/**
 * Search and cursor paging for list screens. Typing is debounced, a new query or refresh cancels the request in
 * flight, and a failed page keeps the items already shown. It is not a ViewModel: a ViewModel creates one with its
 * `viewModelScope` and exposes [state].
 */
@OptIn(FlowPreview::class)
class PagedList<T>(
    private val scope: CoroutineScope,
    private val debounceMillis: Long = DEFAULT_DEBOUNCE_MILLIS,
    private val load: suspend (query: String, cursor: String?) -> Result<Page<T>>,
) {
    private val mutableState = MutableStateFlow(PagedListState<T>())
    val state: StateFlow<PagedListState<T>> = mutableState.asStateFlow()

    private val typedQuery = MutableStateFlow("")
    private var job: Job? = null
    private var nextCursor: String? = null

    init {
        scope.launch {
            typedQuery.drop(1).debounce(debounceMillis).collect { refresh() }
        }
    }

    fun setQuery(query: String) {
        mutableState.update { it.copy(query = query) }
        typedQuery.value = query
    }

    /** Loads the first page again for the current query. */
    fun refresh() {
        job?.cancel()
        nextCursor = null
        mutableState.update { it.copy(loading = true, loadingMore = false, endReached = false, error = null) }
        job = scope.launch {
            val result = load(mutableState.value.query, null)
            ensureActive()
            when (result) {
                is Result.Success -> {
                    nextCursor = result.data.nextCursor
                    mutableState.update {
                        it.copy(items = result.data.items, loading = false, endReached = result.data.nextCursor == null)
                    }
                }
                is Result.Error -> mutableState.update { it.copy(items = emptyList(), loading = false, error = result.error) }
            }
        }
    }

    /** Appends the next page; does nothing while a request runs or after the last page. */
    fun loadMore() {
        val cursor = nextCursor ?: return
        val current = mutableState.value
        if (current.loading || current.loadingMore || current.endReached || job?.isActive == true) return
        mutableState.update { it.copy(loadingMore = true, error = null) }
        job = scope.launch {
            val result = load(mutableState.value.query, cursor)
            ensureActive()
            when (result) {
                is Result.Success -> {
                    nextCursor = result.data.nextCursor
                    mutableState.update {
                        it.copy(
                            items = it.items + result.data.items,
                            loadingMore = false,
                            endReached = result.data.nextCursor == null,
                        )
                    }
                }
                is Result.Error -> mutableState.update { it.copy(loadingMore = false, error = result.error) }
            }
        }
    }

    companion object {
        const val DEFAULT_DEBOUNCE_MILLIS = 300L
    }
}
