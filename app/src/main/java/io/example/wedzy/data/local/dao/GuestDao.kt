package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.GuestSide
import io.example.wedzy.data.model.RsvpStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GuestDao {
    
    @Query("SELECT * FROM guests WHERE userId = :userId ORDER BY firstName, lastName")
    fun getAllGuests(userId: String): Flow<List<Guest>>
    
    @Query("SELECT * FROM guests WHERE userId = :userId AND rsvpStatus = :status ORDER BY firstName, lastName")
    fun getGuestsByRsvpStatus(userId: String, status: RsvpStatus): Flow<List<Guest>>
    
    @Query("SELECT * FROM guests WHERE userId = :userId AND side = :side ORDER BY firstName, lastName")
    fun getGuestsBySide(userId: String, side: GuestSide): Flow<List<Guest>>
    
    @Query("SELECT * FROM guests WHERE id = :id")
    suspend fun getGuestById(id: Long): Guest?
    
    @Query("SELECT COUNT(*) FROM guests WHERE userId = :userId")
    fun getTotalGuestCount(userId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM guests WHERE userId = :userId AND rsvpStatus = :status")
    fun getGuestCountByStatus(userId: String, status: RsvpStatus): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM guests WHERE userId = :userId AND rsvpStatus = 'CONFIRMED'")
    fun getConfirmedGuestCount(userId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM guests WHERE userId = :userId AND plusOneConfirmed = 1")
    fun getConfirmedPlusOneCount(userId: String): Flow<Int>
    
    @Query("SELECT * FROM guests WHERE userId = :userId AND tableNumber = :tableNumber ORDER BY firstName")
    fun getGuestsByTable(userId: String, tableNumber: Int): Flow<List<Guest>>
    
    @Query("SELECT * FROM guests WHERE userId = :userId AND giftReceived = 1 ORDER BY firstName")
    fun getGuestsWithGifts(userId: String): Flow<List<Guest>>
    
    @Query("SELECT * FROM guests WHERE userId = :userId AND thankYouSent = 0 AND giftReceived = 1")
    fun getGuestsNeedingThankYou(userId: String): Flow<List<Guest>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: Guest): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuests(guests: List<Guest>)
    
    @Update
    suspend fun updateGuest(guest: Guest)
    
    @Delete
    suspend fun deleteGuest(guest: Guest)
    
    @Query("DELETE FROM guests WHERE id = :id")
    suspend fun deleteGuestById(id: Long)
    
    @Query("DELETE FROM guests WHERE userId = :userId")
    suspend fun deleteAllGuests(userId: String)
}
