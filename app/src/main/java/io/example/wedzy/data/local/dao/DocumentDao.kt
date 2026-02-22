package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Document
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllDocuments(userId: String): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocumentById(documentId: Long): Document?
    
    @Query("SELECT * FROM documents WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getDocumentsByCategory(userId: String, category: String): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE userId = :userId AND vendorId = :vendorId ORDER BY createdAt DESC")
    fun getDocumentsByVendor(userId: String, vendorId: Long): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteDocuments(userId: String): Flow<List<Document>>
    
    @Query("SELECT SUM(fileSize) FROM documents WHERE userId = :userId")
    fun getTotalStorageUsed(userId: String): Flow<Long?>
    
    @Query("SELECT COUNT(*) FROM documents WHERE userId = :userId")
    fun getDocumentCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long
    
    @Update
    suspend fun updateDocument(document: Document)
    
    @Delete
    suspend fun deleteDocument(document: Document)
    
    @Query("UPDATE documents SET isFavorite = :isFavorite WHERE id = :documentId")
    suspend fun updateFavoriteStatus(documentId: Long, isFavorite: Boolean)
    
    @Query("DELETE FROM documents WHERE userId = :userId")
    suspend fun deleteAllDocuments(userId: String)
}
