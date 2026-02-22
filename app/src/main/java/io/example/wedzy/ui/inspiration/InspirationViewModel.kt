package io.example.wedzy.ui.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Inspiration
import io.example.wedzy.data.model.InspirationCategory
import io.example.wedzy.data.repository.InspirationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InspirationUiState(
    val inspirations: List<Inspiration> = emptyList(),
    val filteredInspirations: List<Inspiration> = emptyList(),
    val selectedCategory: InspirationCategory? = null,
    val showFavoritesOnly: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val inspirationRepository: InspirationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InspirationUiState())
    val uiState: StateFlow<InspirationUiState> = _uiState.asStateFlow()
    
    init {
        loadInspirations()
    }
    
    private fun loadInspirations() {
        viewModelScope.launch {
            inspirationRepository.getAllInspirations().collect { inspirations ->
                _uiState.update { state ->
                    state.copy(
                        inspirations = inspirations,
                        filteredInspirations = filterInspirations(
                            inspirations, 
                            state.selectedCategory, 
                            state.showFavoritesOnly
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun setCategory(category: InspirationCategory?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredInspirations = filterInspirations(
                    state.inspirations, 
                    category, 
                    state.showFavoritesOnly
                )
            )
        }
    }
    
    fun toggleFavoritesOnly() {
        _uiState.update { state ->
            val newShowFavorites = !state.showFavoritesOnly
            state.copy(
                showFavoritesOnly = newShowFavorites,
                filteredInspirations = filterInspirations(
                    state.inspirations, 
                    state.selectedCategory, 
                    newShowFavorites
                )
            )
        }
    }
    
    private fun filterInspirations(
        inspirations: List<Inspiration>,
        category: InspirationCategory?,
        favoritesOnly: Boolean
    ): List<Inspiration> {
        return inspirations.filter { inspiration ->
            val categoryMatch = category == null || inspiration.category == category
            val favoriteMatch = !favoritesOnly || inspiration.isFavorite
            categoryMatch && favoriteMatch
        }
    }
    
    fun toggleFavorite(inspiration: Inspiration) {
        viewModelScope.launch {
            inspirationRepository.toggleFavorite(inspiration.id, !inspiration.isFavorite)
        }
    }
    
    fun deleteInspiration(inspiration: Inspiration) {
        viewModelScope.launch {
            inspirationRepository.deleteInspiration(inspiration)
        }
    }
    
    fun addInspiration(
        title: String,
        category: InspirationCategory,
        imageUrl: String,
        notes: String
    ) {
        viewModelScope.launch {
            val inspiration = Inspiration(
                title = title.trim(),
                category = category,
                imageUrl = imageUrl.trim(),
                notes = notes.trim()
            )
            inspirationRepository.insertInspiration(inspiration)
        }
    }
    
    fun updateInspiration(inspiration: Inspiration) {
        viewModelScope.launch {
            inspirationRepository.updateInspiration(inspiration)
        }
    }
}
