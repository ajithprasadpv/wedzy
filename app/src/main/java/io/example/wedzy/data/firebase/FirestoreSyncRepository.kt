package io.example.wedzy.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.example.wedzy.data.local.dao.BudgetDao
import io.example.wedzy.data.local.dao.GuestDao
import io.example.wedzy.data.local.dao.TaskDao
import io.example.wedzy.data.local.dao.VendorDao
import io.example.wedzy.data.local.dao.WeddingEventDao
import io.example.wedzy.data.local.dao.WeddingProfileDao
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.GuestRelation
import io.example.wedzy.data.model.GuestSide
import io.example.wedzy.data.model.PaymentStatus
import io.example.wedzy.data.model.RsvpStatus
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskCategory
import io.example.wedzy.data.model.TaskPriority
import io.example.wedzy.data.model.TaskStatus
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.model.VendorStatus
import io.example.wedzy.data.model.WeddingEvent
import io.example.wedzy.data.model.WeddingProfile
import io.example.wedzy.data.model.EventStatus
import io.example.wedzy.data.model.EventType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val vendorDao: VendorDao,
    private val guestDao: GuestDao,
    private val budgetDao: BudgetDao,
    private val taskDao: TaskDao,
    private val eventDao: WeddingEventDao,
    private val profileDao: WeddingProfileDao
) {
    private val TAG = "FirestoreSyncRepository"

    private val userId get() = auth.currentUser?.uid

    private fun userDoc() = userId?.let { firestore.collection("userData").document(it) }

    // ─── Vendor ───────────────────────────────────────────────────────────────

    suspend fun syncVendorToCloud(vendor: Vendor) {
        val uid = userId ?: return
        try {
            val doc = userDoc()!!.collection("vendors").document(vendor.id.toString())
            doc.set(vendor.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Vendor synced: ${vendor.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync vendor ${vendor.id}: ${e.message}", e)
        }
    }

    suspend fun deleteVendorFromCloud(vendorId: Long) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("vendors").document(vendorId.toString()).delete().await()
            Log.d(TAG, "Vendor deleted from cloud: $vendorId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete vendor $vendorId: ${e.message}", e)
        }
    }

    // ─── Guest ────────────────────────────────────────────────────────────────

    suspend fun syncGuestToCloud(guest: Guest) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("guests").document(guest.id.toString())
                .set(guest.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Guest synced: ${guest.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync guest ${guest.id}: ${e.message}", e)
        }
    }

    suspend fun deleteGuestFromCloud(guestId: Long) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("guests").document(guestId.toString()).delete().await()
            Log.d(TAG, "Guest deleted from cloud: $guestId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete guest $guestId: ${e.message}", e)
        }
    }

    // ─── Budget ───────────────────────────────────────────────────────────────

    suspend fun syncBudgetItemToCloud(item: BudgetItem) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("budgetItems").document(item.id.toString())
                .set(item.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "BudgetItem synced: ${item.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync budget item ${item.id}: ${e.message}", e)
        }
    }

    suspend fun deleteBudgetItemFromCloud(itemId: Long) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("budgetItems").document(itemId.toString()).delete().await()
            Log.d(TAG, "BudgetItem deleted from cloud: $itemId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete budget item $itemId: ${e.message}", e)
        }
    }

    // ─── Task ─────────────────────────────────────────────────────────────────

    suspend fun syncTaskToCloud(task: Task) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("tasks").document(task.id.toString())
                .set(task.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Task synced: ${task.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync task ${task.id}: ${e.message}", e)
        }
    }

    suspend fun deleteTaskFromCloud(taskId: Long) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("tasks").document(taskId.toString()).delete().await()
            Log.d(TAG, "Task deleted from cloud: $taskId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete task $taskId: ${e.message}", e)
        }
    }

    // ─── Event ────────────────────────────────────────────────────────────────

    suspend fun syncEventToCloud(event: WeddingEvent) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("events").document(event.id.toString())
                .set(event.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Event synced: ${event.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync event ${event.id}: ${e.message}", e)
        }
    }

    suspend fun deleteEventFromCloud(eventId: Long) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("events").document(eventId.toString()).delete().await()
            Log.d(TAG, "Event deleted from cloud: $eventId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete event $eventId: ${e.message}", e)
        }
    }

    // ─── Wedding Profile ──────────────────────────────────────────────────────

    suspend fun syncProfileToCloud(profile: WeddingProfile) {
        val uid = userId ?: return
        try {
            userDoc()!!.collection("profile").document("main")
                .set(profile.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Profile synced")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync profile: ${e.message}", e)
        }
    }

    // ─── Restore all data from Firestore to Room ──────────────────────────────

    suspend fun restoreFromCloud() {
        val uid = userId ?: return
        Log.d(TAG, "Starting cloud restore for user: $uid")
        try {
            restoreVendors(uid)
            restoreGuests(uid)
            restoreBudgetItems(uid)
            restoreTasks(uid)
            restoreEvents(uid)
            restoreProfile(uid)
            Log.d(TAG, "Cloud restore complete")
        } catch (e: Exception) {
            Log.e(TAG, "Cloud restore failed: ${e.message}", e)
        }
    }

    private suspend fun restoreVendors(uid: String) {
        val docs = userDoc()!!.collection("vendors").get().await()
        val vendors = docs.documents.mapNotNull { it.toVendor(uid) }
        if (vendors.isNotEmpty()) {
            vendorDao.insertVendors(vendors)
            Log.d(TAG, "Restored ${vendors.size} vendors")
        }
    }

    private suspend fun restoreGuests(uid: String) {
        val docs = userDoc()!!.collection("guests").get().await()
        val guests = docs.documents.mapNotNull { it.toGuest(uid) }
        if (guests.isNotEmpty()) {
            guestDao.insertGuests(guests)
            Log.d(TAG, "Restored ${guests.size} guests")
        }
    }

    private suspend fun restoreBudgetItems(uid: String) {
        val docs = userDoc()!!.collection("budgetItems").get().await()
        val items = docs.documents.mapNotNull { it.toBudgetItem(uid) }
        if (items.isNotEmpty()) {
            budgetDao.insertBudgetItems(items)
            Log.d(TAG, "Restored ${items.size} budget items")
        }
    }

    private suspend fun restoreTasks(uid: String) {
        val docs = userDoc()!!.collection("tasks").get().await()
        val tasks = docs.documents.mapNotNull { it.toTask(uid) }
        if (tasks.isNotEmpty()) {
            taskDao.insertTasks(tasks)
            Log.d(TAG, "Restored ${tasks.size} tasks")
        }
    }

    private suspend fun restoreEvents(uid: String) {
        val docs = userDoc()!!.collection("events").get().await()
        val events = docs.documents.mapNotNull { it.toEvent(uid) }
        if (events.isNotEmpty()) {
            eventDao.insertEvents(events)
            Log.d(TAG, "Restored ${events.size} events")
        }
    }

    private suspend fun restoreProfile(uid: String) {
        val doc = userDoc()!!.collection("profile").document("main").get().await()
        val profile = doc.toProfile(uid) ?: return
        profileDao.insertProfile(profile)
        Log.d(TAG, "Restored wedding profile")
    }
}

// ─── Extension: Model → Map ───────────────────────────────────────────────────

private fun Vendor.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "name" to name,
    "category" to category.name,
    "contactPerson" to contactPerson,
    "email" to email,
    "phone" to phone,
    "website" to website,
    "address" to address,
    "status" to status.name,
    "quotedPrice" to quotedPrice,
    "agreedPrice" to agreedPrice,
    "depositAmount" to depositAmount,
    "depositPaid" to depositPaid,
    "contractUri" to contractUri,
    "rating" to rating,
    "notes" to notes,
    "meetingDate" to meetingDate,
    "createdAt" to createdAt
)

private fun Guest.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "firstName" to firstName,
    "lastName" to lastName,
    "email" to email,
    "phone" to phone,
    "side" to side.name,
    "relation" to relation.name,
    "rsvpStatus" to rsvpStatus.name,
    "plusOneAllowed" to plusOneAllowed,
    "plusOneName" to plusOneName,
    "plusOneConfirmed" to plusOneConfirmed,
    "dietaryRestrictions" to dietaryRestrictions,
    "specialRequirements" to specialRequirements,
    "tableNumber" to tableNumber,
    "giftReceived" to giftReceived,
    "giftDescription" to giftDescription,
    "thankYouSent" to thankYouSent,
    "notes" to notes,
    "createdAt" to createdAt
)

private fun BudgetItem.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "name" to name,
    "category" to category.name,
    "estimatedCost" to estimatedCost,
    "actualCost" to actualCost,
    "paidAmount" to paidAmount,
    "paymentStatus" to paymentStatus.name,
    "vendorId" to vendorId,
    "notes" to notes,
    "receiptUri" to receiptUri,
    "dueDate" to dueDate,
    "createdAt" to createdAt
)

private fun Task.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "title" to title,
    "description" to description,
    "dueDate" to dueDate,
    "priority" to priority.name,
    "status" to status.name,
    "category" to category.name,
    "assignedTo" to assignedTo,
    "notes" to notes,
    "isFromTemplate" to isFromTemplate,
    "createdAt" to createdAt,
    "completedAt" to completedAt
)

private fun WeddingEvent.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "title" to title,
    "description" to description,
    "eventType" to eventType.name,
    "status" to status.name,
    "startDateTime" to startDateTime,
    "endDateTime" to endDateTime,
    "location" to location,
    "address" to address,
    "notes" to notes,
    "reminderMinutesBefore" to reminderMinutesBefore,
    "isAllDay" to isAllDay,
    "color" to color,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun WeddingProfile.toMap() = mapOf(
    "id" to id,
    "userId" to userId,
    "brideName" to brideName,
    "groomName" to groomName,
    "weddingDate" to weddingDate,
    "venueName" to venueName,
    "venueAddress" to venueAddress,
    "totalBudget" to totalBudget,
    "currency" to currency,
    "profileImageUri" to profileImageUri,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

// ─── Extension: DocumentSnapshot → Model ─────────────────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.toVendor(uid: String): Vendor? {
    return try {
        Vendor(
            id = getLong("id") ?: 0L,
            userId = uid,
            name = getString("name") ?: return null,
            category = VendorCategory.valueOf(getString("category") ?: "OTHER"),
            contactPerson = getString("contactPerson") ?: "",
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",
            website = getString("website") ?: "",
            address = getString("address") ?: "",
            status = VendorStatus.valueOf(getString("status") ?: "RESEARCHING"),
            quotedPrice = getDouble("quotedPrice") ?: 0.0,
            agreedPrice = getDouble("agreedPrice") ?: 0.0,
            depositAmount = getDouble("depositAmount") ?: 0.0,
            depositPaid = getBoolean("depositPaid") ?: false,
            contractUri = getString("contractUri"),
            rating = getLong("rating")?.toInt() ?: 0,
            notes = getString("notes") ?: "",
            meetingDate = getLong("meetingDate"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toGuest(uid: String): Guest? {
    return try {
        Guest(
            id = getLong("id") ?: 0L,
            userId = uid,
            firstName = getString("firstName") ?: return null,
            lastName = getString("lastName") ?: "",
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",
            side = GuestSide.valueOf(getString("side") ?: "MUTUAL"),
            relation = GuestRelation.valueOf(getString("relation") ?: "OTHER"),
            rsvpStatus = RsvpStatus.valueOf(getString("rsvpStatus") ?: "PENDING"),
            plusOneAllowed = getBoolean("plusOneAllowed") ?: false,
            plusOneName = getString("plusOneName"),
            plusOneConfirmed = getBoolean("plusOneConfirmed") ?: false,
            dietaryRestrictions = getString("dietaryRestrictions") ?: "",
            specialRequirements = getString("specialRequirements") ?: "",
            tableNumber = getLong("tableNumber")?.toInt(),
            giftReceived = getBoolean("giftReceived") ?: false,
            giftDescription = getString("giftDescription") ?: "",
            thankYouSent = getBoolean("thankYouSent") ?: false,
            notes = getString("notes") ?: "",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toBudgetItem(uid: String): BudgetItem? {
    return try {
        BudgetItem(
            id = getLong("id") ?: 0L,
            userId = uid,
            name = getString("name") ?: return null,
            category = BudgetCategory.valueOf(getString("category") ?: "OTHER"),
            estimatedCost = getDouble("estimatedCost") ?: 0.0,
            actualCost = getDouble("actualCost") ?: 0.0,
            paidAmount = getDouble("paidAmount") ?: 0.0,
            paymentStatus = PaymentStatus.valueOf(getString("paymentStatus") ?: "NOT_PAID"),
            vendorId = getLong("vendorId"),
            notes = getString("notes") ?: "",
            receiptUri = getString("receiptUri"),
            dueDate = getLong("dueDate"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toTask(uid: String): Task? {
    return try {
        Task(
            id = getLong("id") ?: 0L,
            userId = uid,
            title = getString("title") ?: return null,
            description = getString("description") ?: "",
            dueDate = getLong("dueDate"),
            priority = TaskPriority.valueOf(getString("priority") ?: "MEDIUM"),
            status = TaskStatus.valueOf(getString("status") ?: "PENDING"),
            category = TaskCategory.valueOf(getString("category") ?: "OTHER"),
            assignedTo = getString("assignedTo"),
            notes = getString("notes") ?: "",
            isFromTemplate = getBoolean("isFromTemplate") ?: false,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            completedAt = getLong("completedAt")
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toEvent(uid: String): WeddingEvent? {
    return try {
        WeddingEvent(
            id = getLong("id") ?: 0L,
            userId = uid,
            title = getString("title") ?: return null,
            description = getString("description") ?: "",
            eventType = EventType.valueOf(getString("eventType") ?: "OTHER"),
            status = EventStatus.valueOf(getString("status") ?: "SCHEDULED"),
            startDateTime = getLong("startDateTime") ?: return null,
            endDateTime = getLong("endDateTime"),
            location = getString("location") ?: "",
            address = getString("address") ?: "",
            notes = getString("notes") ?: "",
            reminderMinutesBefore = getLong("reminderMinutesBefore")?.toInt() ?: 60,
            isAllDay = getBoolean("isAllDay") ?: false,
            color = getString("color") ?: "#E91E63",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toProfile(uid: String): WeddingProfile? {
    return try {
        WeddingProfile(
            id = getLong("id") ?: 0L,
            userId = uid,
            brideName = getString("brideName") ?: "",
            groomName = getString("groomName") ?: "",
            weddingDate = getLong("weddingDate") ?: 0L,
            venueName = getString("venueName") ?: "",
            venueAddress = getString("venueAddress") ?: "",
            totalBudget = getDouble("totalBudget") ?: 0.0,
            currency = getString("currency") ?: "USD",
            profileImageUri = getString("profileImageUri"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) { null }
}
