package io.example.wedzy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.example.wedzy.data.model.SeatAssignment
import io.example.wedzy.data.model.SeatingTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatingDao {
    @Query("SELECT * FROM seating_tables WHERE userId = :userId ORDER BY tableNumber ASC")
    fun getAllTables(userId: String): Flow<List<SeatingTable>>
    
    @Query("SELECT * FROM seating_tables WHERE id = :tableId")
    suspend fun getTableById(tableId: Long): SeatingTable?
    
    @Query("SELECT COUNT(*) FROM seating_tables WHERE userId = :userId")
    fun getTableCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: SeatingTable): Long
    
    @Update
    suspend fun updateTable(table: SeatingTable)
    
    @Delete
    suspend fun deleteTable(table: SeatingTable)
    
    @Query("SELECT * FROM seat_assignments WHERE userId = :userId ORDER BY tableId, seatNumber ASC")
    fun getAllSeatAssignments(userId: String): Flow<List<SeatAssignment>>
    
    @Query("SELECT * FROM seat_assignments WHERE userId = :userId AND tableId = :tableId ORDER BY seatNumber ASC")
    fun getSeatAssignmentsByTable(userId: String, tableId: Long): Flow<List<SeatAssignment>>
    
    @Query("SELECT * FROM seat_assignments WHERE guestId = :guestId")
    suspend fun getSeatAssignmentByGuest(guestId: Long): SeatAssignment?
    
    @Query("SELECT COUNT(*) FROM seat_assignments WHERE userId = :userId AND tableId = :tableId")
    fun getSeatedCountForTable(userId: String, tableId: Long): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM seat_assignments WHERE userId = :userId")
    fun getTotalSeatedCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeatAssignment(assignment: SeatAssignment): Long
    
    @Update
    suspend fun updateSeatAssignment(assignment: SeatAssignment)
    
    @Delete
    suspend fun deleteSeatAssignment(assignment: SeatAssignment)
    
    @Query("DELETE FROM seat_assignments WHERE guestId = :guestId")
    suspend fun removeSeatAssignmentByGuest(guestId: Long)
    
    @Query("DELETE FROM seat_assignments WHERE tableId = :tableId")
    suspend fun clearTableAssignments(tableId: Long)
    
    @Query("DELETE FROM seating_tables WHERE userId = :userId")
    suspend fun deleteAllTables(userId: String)
    
    @Query("DELETE FROM seat_assignments WHERE userId = :userId")
    suspend fun deleteAllSeatAssignments(userId: String)
}
