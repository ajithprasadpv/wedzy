package io.example.wedzy.data.repository

import io.example.wedzy.data.auth.UserSession
import io.example.wedzy.data.local.dao.SeatingDao
import io.example.wedzy.data.model.SeatAssignment
import io.example.wedzy.data.model.SeatingTable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeatingRepository @Inject constructor(
    private val seatingDao: SeatingDao,
    private val userSession: UserSession
) {
    fun getAllTables(): Flow<List<SeatingTable>> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getAllTables(userId)
    }
    
    suspend fun getTableById(tableId: Long): SeatingTable? = seatingDao.getTableById(tableId)
    
    fun getTableCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getTableCount(userId)
    }
    
    suspend fun insertTable(table: SeatingTable): Long {
        val userId = userSession.getCurrentUserId()
        return seatingDao.insertTable(table.copy(userId = userId))
    }
    
    suspend fun updateTable(table: SeatingTable) {
        val userId = userSession.getCurrentUserId()
        seatingDao.updateTable(table.copy(userId = userId))
    }
    
    suspend fun deleteTable(table: SeatingTable) = seatingDao.deleteTable(table)
    
    fun getAllSeatAssignments(): Flow<List<SeatAssignment>> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getAllSeatAssignments(userId)
    }
    
    fun getSeatAssignmentsByTable(tableId: Long): Flow<List<SeatAssignment>> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getSeatAssignmentsByTable(userId, tableId)
    }
    
    suspend fun getSeatAssignmentByGuest(guestId: Long): SeatAssignment? = 
        seatingDao.getSeatAssignmentByGuest(guestId)
    
    fun getSeatedCountForTable(tableId: Long): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getSeatedCountForTable(userId, tableId)
    }
    
    fun getTotalSeatedCount(): Flow<Int> {
        val userId = userSession.getCurrentUserId()
        return seatingDao.getTotalSeatedCount(userId)
    }
    
    suspend fun assignSeat(assignment: SeatAssignment): Long {
        val userId = userSession.getCurrentUserId()
        return seatingDao.insertSeatAssignment(assignment.copy(userId = userId))
    }
    
    suspend fun updateSeatAssignment(assignment: SeatAssignment) {
        val userId = userSession.getCurrentUserId()
        seatingDao.updateSeatAssignment(assignment.copy(userId = userId))
    }
    
    suspend fun removeSeatAssignment(assignment: SeatAssignment) = seatingDao.deleteSeatAssignment(assignment)
    
    suspend fun removeGuestFromSeat(guestId: Long) = seatingDao.removeSeatAssignmentByGuest(guestId)
    
    suspend fun clearTable(tableId: Long) = seatingDao.clearTableAssignments(tableId)
    
    suspend fun clearUserData() {
        val userId = userSession.getCurrentUserId()
        if (userId.isNotEmpty()) {
            seatingDao.deleteAllTables(userId)
            seatingDao.deleteAllSeatAssignments(userId)
        }
    }
}
