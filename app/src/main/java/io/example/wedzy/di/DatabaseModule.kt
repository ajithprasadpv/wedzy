package io.example.wedzy.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.example.wedzy.data.local.WedzyDatabase
import io.example.wedzy.data.local.dao.AIRecommendationDao
import io.example.wedzy.data.local.dao.BudgetDao
import io.example.wedzy.data.local.dao.CollaboratorDao
import io.example.wedzy.data.local.dao.DocumentDao
import io.example.wedzy.data.local.dao.GuestDao
import io.example.wedzy.data.local.dao.InspirationDao
import io.example.wedzy.data.local.dao.MarketplaceDao
import io.example.wedzy.data.local.dao.SeatingDao
import io.example.wedzy.data.local.dao.TaskDao
import io.example.wedzy.data.local.dao.TemplateDao
import io.example.wedzy.data.local.dao.VendorDao
import io.example.wedzy.data.local.dao.WeddingEventDao
import io.example.wedzy.data.local.dao.WeddingProfileDao
import io.example.wedzy.notifications.EventNotificationScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideEventNotificationScheduler(
        @ApplicationContext context: Context
    ): EventNotificationScheduler {
        return EventNotificationScheduler(context)
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WedzyDatabase {
        return Room.databaseBuilder(
            context,
            WedzyDatabase::class.java,
            WedzyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideWeddingProfileDao(database: WedzyDatabase): WeddingProfileDao {
        return database.weddingProfileDao()
    }
    
    @Provides
    fun provideTaskDao(database: WedzyDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    fun provideBudgetDao(database: WedzyDatabase): BudgetDao {
        return database.budgetDao()
    }
    
    @Provides
    fun provideGuestDao(database: WedzyDatabase): GuestDao {
        return database.guestDao()
    }
    
    @Provides
    fun provideVendorDao(database: WedzyDatabase): VendorDao {
        return database.vendorDao()
    }
    
    @Provides
    fun provideWeddingEventDao(database: WedzyDatabase): WeddingEventDao {
        return database.weddingEventDao()
    }
    
    @Provides
    fun provideDocumentDao(database: WedzyDatabase): DocumentDao {
        return database.documentDao()
    }
    
    @Provides
    fun provideSeatingDao(database: WedzyDatabase): SeatingDao {
        return database.seatingDao()
    }
    
    @Provides
    fun provideInspirationDao(database: WedzyDatabase): InspirationDao {
        return database.inspirationDao()
    }
    
    @Provides
    fun provideCollaboratorDao(database: WedzyDatabase): CollaboratorDao {
        return database.collaboratorDao()
    }
    
    @Provides
    fun provideTemplateDao(database: WedzyDatabase): TemplateDao {
        return database.templateDao()
    }
    
    @Provides
    fun provideMarketplaceDao(database: WedzyDatabase): MarketplaceDao {
        return database.marketplaceDao()
    }
    
    @Provides
    fun provideAIRecommendationDao(database: WedzyDatabase): AIRecommendationDao {
        return database.aiRecommendationDao()
    }
}
