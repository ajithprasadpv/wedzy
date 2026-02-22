package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.local.dao.DocumentDao
import io.example.wedzy.data.model.Document
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val userSession: UserSession
) {
    fun getAllDocuments(): Flow<List<Document>> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getAllDocuments(userId)
    }
    
    suspend fun getDocumentById(documentId: Long): Document? = documentDao.getDocumentById(documentId)
    
    fun getDocumentsByCategory(category: String): Flow<List<Document>> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getDocumentsByCategory(userId, category)
    }
    
    fun getDocumentsByVendor(vendorId: Long): Flow<List<Document>> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getDocumentsByVendor(userId, vendorId)
    }
    
    fun getFavoriteDocuments(): Flow<List<Document>> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getFavoriteDocuments(userId)
    }
    
    fun getTotalStorageUsed(): Flow<Long?> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getTotalStorageUsed(userId)
    }
    
    fun getDocumentCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return documentDao.getDocumentCount(userId)
    }
    
    suspend fun insertDocument(document: Document): Long {
        val userId = userSession.getCurrentUserId()
        return documentDao.insertDocument(document.copy(userId = userId))
    }
    
    suspend fun updateDocument(document: Document) {
        val userId = userSession.getCurrentUserId()
        documentDao.updateDocument(document.copy(userId = userId))
    }
    
    suspend fun deleteDocument(document: Document) = documentDao.deleteDocument(document)
    
    suspend fun toggleFavorite(documentId: Long, isFavorite: Boolean) = 
        documentDao.updateFavoriteStatus(documentId, isFavorite)
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            documentDao.deleteAllDocuments(userId)
        }
    }
}
