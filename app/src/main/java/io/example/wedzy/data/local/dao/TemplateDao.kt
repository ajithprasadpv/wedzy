package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Template
import io.example.wedzy.data.model.UserTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<Template>>
    
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Long): Template?
    
    @Query("SELECT * FROM templates WHERE type = :type ORDER BY rating DESC")
    fun getTemplatesByType(type: String): Flow<List<Template>>
    
    @Query("SELECT * FROM templates WHERE isPremium = 0 ORDER BY rating DESC")
    fun getFreeTemplates(): Flow<List<Template>>
    
    @Query("SELECT * FROM templates WHERE isPremium = 1 ORDER BY rating DESC")
    fun getPremiumTemplates(): Flow<List<Template>>
    
    @Query("SELECT * FROM templates WHERE isDownloaded = 1 ORDER BY name ASC")
    fun getDownloadedTemplates(): Flow<List<Template>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: Template): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<Template>)
    
    @Update
    suspend fun updateTemplate(template: Template)
    
    @Delete
    suspend fun deleteTemplate(template: Template)
    
    @Query("SELECT * FROM user_templates ORDER BY createdAt DESC")
    fun getAllUserTemplates(): Flow<List<UserTemplate>>
    
    @Query("SELECT * FROM user_templates WHERE id = :userTemplateId")
    suspend fun getUserTemplateById(userTemplateId: Long): UserTemplate?
    
    @Query("SELECT * FROM user_templates WHERE templateId = :templateId")
    fun getUserTemplatesByTemplate(templateId: Long): Flow<List<UserTemplate>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTemplate(userTemplate: UserTemplate): Long
    
    @Update
    suspend fun updateUserTemplate(userTemplate: UserTemplate)
    
    @Delete
    suspend fun deleteUserTemplate(userTemplate: UserTemplate)
}
