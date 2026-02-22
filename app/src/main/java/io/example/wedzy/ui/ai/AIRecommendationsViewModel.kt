package io.example.wedzy.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.AIRecommendation
import io.example.wedzy.data.model.RecommendationPriority
import io.example.wedzy.data.model.RecommendationType
import io.example.wedzy.data.repository.AIRecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIRecommendationsUiState(
    val recommendations: List<AIRecommendation> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AIRecommendationsViewModel @Inject constructor(
    private val aiRecommendationRepository: AIRecommendationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIRecommendationsUiState())
    val uiState: StateFlow<AIRecommendationsUiState> = _uiState.asStateFlow()
    
    init {
        loadRecommendations()
        generateSampleRecommendations()
    }
    
    private fun loadRecommendations() {
        viewModelScope.launch {
            aiRecommendationRepository.getActiveRecommendations().collect { recommendations ->
                _uiState.update { it.copy(recommendations = recommendations, isLoading = false) }
            }
        }
        viewModelScope.launch {
            aiRecommendationRepository.getUnreadCount().collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }
    }
    
    private fun generateSampleRecommendations() {
        viewModelScope.launch {
            val existing = _uiState.value.recommendations
            if (existing.isEmpty()) {
                val samples = listOf(
                    AIRecommendation(
                        type = RecommendationType.BUDGET_TIP,
                        title = "Budget Optimization",
                        description = "Consider allocating more to photography - couples rate it as their most valued expense.",
                        reason = "Based on wedding trends analysis",
                        priority = RecommendationPriority.MEDIUM,
                        confidence = 0.85f
                    ),
                    AIRecommendation(
                        type = RecommendationType.TASK,
                        title = "Send Save-the-Dates",
                        description = "It's recommended to send save-the-dates 6-8 months before your wedding.",
                        reason = "Based on your wedding date",
                        priority = RecommendationPriority.HIGH,
                        confidence = 0.92f
                    ),
                    AIRecommendation(
                        type = RecommendationType.VENDOR,
                        title = "Book Your Caterer Soon",
                        description = "Popular caterers get booked 8-12 months in advance. Start reaching out now.",
                        reason = "Based on vendor availability trends",
                        priority = RecommendationPriority.HIGH,
                        confidence = 0.88f
                    )
                )
                aiRecommendationRepository.insertRecommendations(samples)
            }
        }
    }
    
    fun markAsRead(recommendation: AIRecommendation) {
        viewModelScope.launch {
            aiRecommendationRepository.markAsRead(recommendation.id)
        }
    }
    
    fun dismissRecommendation(recommendation: AIRecommendation) {
        viewModelScope.launch {
            aiRecommendationRepository.dismissRecommendation(recommendation.id)
        }
    }
    
    fun actOnRecommendation(recommendation: AIRecommendation) {
        viewModelScope.launch {
            aiRecommendationRepository.markAsActedUpon(recommendation.id)
        }
    }
}
