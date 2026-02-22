package io.example.wedzy.data.repository

import io.example.wedzy.data.local.dao.TemplateDao
import io.example.wedzy.data.model.Template
import io.example.wedzy.data.model.UserTemplate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepository @Inject constructor(
    private val templateDao: TemplateDao
) {
    fun getAllTemplates(): Flow<List<Template>> = templateDao.getAllTemplates()
    
    suspend fun getTemplateById(templateId: Long): Template? = templateDao.getTemplateById(templateId)
    
    fun getTemplatesByType(type: String): Flow<List<Template>> = templateDao.getTemplatesByType(type)
    
    fun getFreeTemplates(): Flow<List<Template>> = templateDao.getFreeTemplates()
    
    fun getPremiumTemplates(): Flow<List<Template>> = templateDao.getPremiumTemplates()
    
    fun getDownloadedTemplates(): Flow<List<Template>> = templateDao.getDownloadedTemplates()
    
    suspend fun insertTemplate(template: Template): Long = templateDao.insertTemplate(template)
    
    suspend fun insertTemplates(templates: List<Template>) = templateDao.insertTemplates(templates)
    
    suspend fun updateTemplate(template: Template) = templateDao.updateTemplate(template)
    
    suspend fun deleteTemplate(template: Template) = templateDao.deleteTemplate(template)
    
    fun getAllUserTemplates(): Flow<List<UserTemplate>> = templateDao.getAllUserTemplates()
    
    suspend fun getUserTemplateById(userTemplateId: Long): UserTemplate? = 
        templateDao.getUserTemplateById(userTemplateId)
    
    fun getUserTemplatesByTemplate(templateId: Long): Flow<List<UserTemplate>> = 
        templateDao.getUserTemplatesByTemplate(templateId)
    
    suspend fun insertUserTemplate(userTemplate: UserTemplate): Long = 
        templateDao.insertUserTemplate(userTemplate)
    
    suspend fun updateUserTemplate(userTemplate: UserTemplate) = 
        templateDao.updateUserTemplate(userTemplate)
    
    suspend fun deleteUserTemplate(userTemplate: UserTemplate) = 
        templateDao.deleteUserTemplate(userTemplate)
}
