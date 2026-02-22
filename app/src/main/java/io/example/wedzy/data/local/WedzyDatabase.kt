package io.example.wedzy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.example.wedzy.data.local.dao.AIRecommendationDao
import io.example.wedzy.data.local.dao.BudgetDao
import io.example.wedzy.data.local.dao.CollaboratorDao
import io.example.wedzy.data.local.dao.DocumentDao
import io.example.wedzy.data.local.dao.GuestDao
import io.example.wedzy.data.local.dao.InspirationDao
import io.example.wedzy.data.local.dao.InviteCodeDao
import io.example.wedzy.data.local.dao.MarketplaceDao
import io.example.wedzy.data.local.dao.SeatingDao
import io.example.wedzy.data.local.dao.TaskDao
import io.example.wedzy.data.local.dao.TaskDependencyDao
import io.example.wedzy.data.local.dao.TemplateDao
import io.example.wedzy.data.local.dao.VendorDao
import io.example.wedzy.data.local.dao.WeddingEventDao
import io.example.wedzy.data.local.dao.WeddingProfileDao
import io.example.wedzy.data.model.AIRecommendation
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.Collaborator
import io.example.wedzy.data.model.Document
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.Inspiration
import io.example.wedzy.data.model.InviteCode
import io.example.wedzy.data.model.MarketplaceVendor
import io.example.wedzy.data.model.SeatAssignment
import io.example.wedzy.data.model.SeatingTable
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.Template
import io.example.wedzy.data.model.UserAIPreferences
import io.example.wedzy.data.model.UserSubscription
import io.example.wedzy.data.model.UserTemplate
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorReview
import io.example.wedzy.data.model.WeddingEvent
import io.example.wedzy.data.model.WeddingProfile
import io.example.wedzy.data.model.TaskDependency
import io.example.wedzy.data.model.RsvpForm
import io.example.wedzy.data.model.VendorComparison

@Database(
    entities = [
        WeddingProfile::class,
        Task::class,
        BudgetItem::class,
        Guest::class,
        Vendor::class,
        WeddingEvent::class,
        Document::class,
        SeatingTable::class,
        SeatAssignment::class,
        Inspiration::class,
        Collaborator::class,
        InviteCode::class,
        Template::class,
        UserTemplate::class,
        MarketplaceVendor::class,
        VendorReview::class,
        AIRecommendation::class,
        UserAIPreferences::class,
        UserSubscription::class,
        TaskDependency::class,
        RsvpForm::class,
        VendorComparison::class
    ],
    version = 8,
    exportSchema = false
)
abstract class WedzyDatabase : RoomDatabase() {
    
    abstract fun weddingProfileDao(): WeddingProfileDao
    abstract fun taskDao(): TaskDao
    abstract fun budgetDao(): BudgetDao
    abstract fun guestDao(): GuestDao
    abstract fun vendorDao(): VendorDao
    abstract fun weddingEventDao(): WeddingEventDao
    abstract fun documentDao(): DocumentDao
    abstract fun seatingDao(): SeatingDao
    abstract fun inspirationDao(): InspirationDao
    abstract fun collaboratorDao(): CollaboratorDao
    abstract fun inviteCodeDao(): InviteCodeDao
    abstract fun templateDao(): TemplateDao
    abstract fun marketplaceDao(): MarketplaceDao
    abstract fun aiRecommendationDao(): AIRecommendationDao
    abstract fun taskDependencyDao(): TaskDependencyDao
    
    companion object {
        const val DATABASE_NAME = "wedzy_database"
    }
}
