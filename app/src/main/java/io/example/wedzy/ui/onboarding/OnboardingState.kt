package io.example.wedzy.ui.onboarding

import io.example.wedzy.data.model.Currency

data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val brideName: String = "",
    val groomName: String = "",
    val weddingDate: Long? = null,
    val totalBudget: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val wantsNotifications: Boolean = true,
    val wantsSampleData: Boolean = false,
    val isCompleting: Boolean = false,
    val error: String? = null
) {
    val currentStepIndex: Int
        get() = OnboardingStep.values().indexOf(currentStep)
    
    val totalSteps: Int
        get() = OnboardingStep.values().size
    
    val progress: Float
        get() = (currentStepIndex + 1).toFloat() / totalSteps.toFloat()
    
    fun canProceed(): Boolean {
        return when (currentStep) {
            OnboardingStep.WELCOME -> true
            OnboardingStep.FEATURES -> true
            OnboardingStep.PROFILE -> brideName.isNotBlank() && groomName.isNotBlank()
            OnboardingStep.WEDDING_DATE -> weddingDate != null
            OnboardingStep.BUDGET -> totalBudget.isNotBlank() && totalBudget.toDoubleOrNull() != null && totalBudget.toDouble() > 0
            OnboardingStep.PERMISSIONS -> true
            OnboardingStep.SAMPLE_DATA -> true
        }
    }
}

enum class OnboardingStep {
    WELCOME,
    FEATURES,
    PROFILE,
    WEDDING_DATE,
    BUDGET,
    PERMISSIONS,
    SAMPLE_DATA
}

data class FeatureHighlight(
    val title: String,
    val description: String,
    val iconName: String
)

val featureHighlights = listOf(
    FeatureHighlight(
        title = "Smart Task Management",
        description = "Generate a complete wedding checklist with 45+ tasks automatically scheduled based on your wedding date",
        iconName = "checklist"
    ),
    FeatureHighlight(
        title = "Budget Tracking",
        description = "Track expenses with industry-standard recommendations and real-time alerts to stay on budget",
        iconName = "budget"
    ),
    FeatureHighlight(
        title = "Guest Management",
        description = "Manage your guest list, track RSVPs, and collect meal preferences with shareable links",
        iconName = "guests"
    ),
    FeatureHighlight(
        title = "Vendor Coordination",
        description = "Compare vendors side-by-side, track contracts, and manage payments all in one place",
        iconName = "vendors"
    ),
    FeatureHighlight(
        title = "Calendar & Timeline",
        description = "Visualize your wedding timeline with a beautiful calendar view and never miss important dates",
        iconName = "calendar"
    )
)
