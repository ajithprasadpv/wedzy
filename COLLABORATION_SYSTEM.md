# Wedding Collaboration System - Implementation Guide

## Overview
This document describes the complete implementation of the wedding collaboration system with WhatsApp invites and unique invite codes.

## Architecture

### Current Implementation: Room Database (Local)
The current implementation uses Room Database for local storage:
- **InviteCode**: Stores unique 10-character codes for each invitee
- **Collaborator**: Links collaborators to invite codes
- **Local-first**: Fast, offline-capable

### Recommended: Hybrid (Room + Firebase)
For production, implement a hybrid approach:

```
┌─────────────────────────────────────────────────┐
│           LOCAL (Room Database)                  │
│  - Personal wedding data (tasks, budget, etc.)  │
│  - Cached collaborator data                      │
│  - Offline-first storage                         │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│        CLOUD (Firebase Firestore)                │
│  - Wedding events (shared across users)         │
│  - Invite codes validation                       │
│  - Real-time collaboration sync                  │
│  - User profiles and joined weddings             │
└─────────────────────────────────────────────────┘
```

## Features Implemented

### 1. Unique Invite Code Generation
- **Format**: 10-character alphanumeric codes (e.g., `tCyCCNuEMc`)
- **Generation**: Random, unique per invitee
- **Storage**: `InviteCode` entity in Room database
- **Tracking**: Stores who was invited, when, and if code was used

### 2. Collaboration Screen Enhancements
- **FAB Menu**: 
  - "Add manually" - Traditional email-based invite
  - "Select from contacts" - WhatsApp-based invite
- **Pending Invites**: Shows invited members waiting to join
- **Active Members**: Displays joined collaborators

### 3. WhatsApp Integration
**Message Template**:
```
Hi! 
I have been using the Wedzy app and I need your help to plan my wedding: https://play.google.com/store/apps/details?id=app.wedzy.android 
Install it and join to my wedding by entering this code: {UNIQUE_CODE} 
See you in the app!
```

**Implementation**:
- Contact picker with search
- Multi-select contacts
- Automatic WhatsApp intent for each selected contact
- Opens WhatsApp with pre-filled message containing unique code

### 4. Database Schema

#### InviteCode Entity
```kotlin
@Entity(tableName = "invite_codes")
data class InviteCode(
    @PrimaryKey val code: String,          // Unique 10-char code
    val weddingId: Long,                   // Wedding event ID
    val invitedName: String,               // Invitee name
    val invitedPhone: String,              // Invitee phone
    val role: CollaboratorRole,            // Assigned role
    val isUsed: Boolean = false,           // Code redemption status
    val usedByUserId: String? = null,      // Who redeemed it
    val createdAt: Long,                   // Creation timestamp
    val usedAt: Long? = null,              // Redemption timestamp
    val expiresAt: Long? = null            // Optional expiration
)
```

#### Updated Collaborator Entity
```kotlin
@Entity(tableName = "collaborators")
data class Collaborator(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val email: String = "",                // Optional
    val phone: String = "",                // Phone for WhatsApp
    val inviteCode: String = "",           // Associated invite code
    val role: CollaboratorRole,
    val isInvitePending: Boolean = true,   // Waiting to join
    val invitedAt: Long,
    val joinedAt: Long? = null
    // ... other fields
)
```

## User Flow

### Inviting Collaborators

1. **Host opens Collaboration screen**
2. **Taps FAB → "Select from contacts"**
3. **System generates unique invite code** for the wedding
4. **Selects multiple contacts** from phone
5. **Taps "Send (X)"**
6. **For each contact**:
   - Unique invite code generated and stored
   - Collaborator record created with pending status
   - WhatsApp opens with pre-filled message
   - Host sends message manually

### Joining a Wedding

**To be implemented** (see below):
1. New user installs app
2. Opens app → sees "Join Wedding" option
3. Enters invite code (e.g., `tCyCCNuEMc`)
4. System validates code
5. If valid:
   - Marks code as used
   - Updates collaborator status to active
   - Syncs wedding data
   - Navigates to wedding dashboard

## Firebase Integration Strategy

### Why Firebase?
- **Real-time sync**: Multiple users editing simultaneously
- **Offline support**: Firebase SDK handles offline mode
- **Authentication**: Secure user management
- **Scalability**: Serverless, auto-scales
- **Cross-platform**: Works on Android, iOS, Web

### Firebase Firestore Structure

```
weddings (collection)
  ├── {weddingId} (document)
      ├── metadata
      │   ├── brideName: string
      │   ├── groomName: string
      │   ├── weddingDate: timestamp
      │   └── ownerId: string
      ├── inviteCodes (subcollection)
      │   ├── {code} (document)
      │       ├── code: string
      │       ├── invitedName: string
      │       ├── invitedPhone: string
      │       ├── role: string
      │       ├── isUsed: boolean
      │       ├── usedBy: string
      │       └── createdAt: timestamp
      ├── collaborators (subcollection)
      │   ├── {userId} (document)
      │       ├── name: string
      │       ├── role: string
      │       ├── permissions: map
      │       └── joinedAt: timestamp
      ├── tasks (subcollection)
      │   └── {taskId} → task data
      ├── budget (subcollection)
      │   └── {itemId} → budget item data
      └── guests (subcollection)
          └── {guestId} → guest data

users (collection)
  ├── {userId} (document)
      ├── name: string
      ├── email: string
      ├── phone: string
      ├── joinedWeddings: array<string>
      │   └── [weddingId1, weddingId2, ...]
      └── ownedWeddings: array<string>
```

### Implementation Steps

#### 1. Add Firebase Dependencies
```gradle
// app/build.gradle
dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-auth-ktx'
}
```

#### 2. Create Firebase Repository
```kotlin
@Singleton
class FirebaseCollaborationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun validateInviteCode(code: String): WeddingInvite? {
        // Query all weddings for this invite code
        val snapshot = firestore.collectionGroup("inviteCodes")
            .whereEqualTo("code", code)
            .whereEqualTo("isUsed", false)
            .get()
            .await()
        
        return snapshot.documents.firstOrNull()?.let {
            WeddingInvite(
                weddingId = it.reference.parent.parent!!.id,
                code = code,
                role = it.getString("role") ?: "FRIEND"
            )
        }
    }
    
    suspend fun joinWedding(weddingId: String, inviteCode: String) {
        val userId = auth.currentUser?.uid ?: return
        
        firestore.runBatch { batch ->
            // Mark code as used
            val codeRef = firestore.collection("weddings")
                .document(weddingId)
                .collection("inviteCodes")
                .document(inviteCode)
            batch.update(codeRef, mapOf(
                "isUsed" to true,
                "usedBy" to userId,
                "usedAt" to FieldValue.serverTimestamp()
            ))
            
            // Add user as collaborator
            val collaboratorRef = firestore.collection("weddings")
                .document(weddingId)
                .collection("collaborators")
                .document(userId)
            batch.set(collaboratorRef, mapOf(
                "userId" to userId,
                "joinedAt" to FieldValue.serverTimestamp()
            ))
            
            // Update user's joined weddings
            val userRef = firestore.collection("users").document(userId)
            batch.update(userRef, mapOf(
                "joinedWeddings" to FieldValue.arrayUnion(weddingId)
            ))
        }.await()
    }
}
```

#### 3. Sync Strategy
- **Write**: Write to both Room (immediate) and Firebase (background)
- **Read**: Read from Room (fast), sync from Firebase (background)
- **Conflicts**: Last-write-wins or custom merge logic

## Next Steps

### 1. Create Invite Code Validation Screen
Create `JoinWeddingScreen.kt`:
- Text field for entering invite code
- Validate button
- Error handling for invalid codes
- Success → navigate to onboarding with wedding data

### 2. Implement Firebase Authentication
- Sign up with email/phone
- Associate userId with invite codes
- Secure access to wedding data

### 3. Add Real-time Sync
- Listen to Firebase changes
- Update Room database
- Show sync status indicator

### 4. Handle Edge Cases
- Expired codes
- Already-used codes
- User already in wedding
- Network errors
- Offline mode

### 5. Testing
- Test invite code generation uniqueness
- Test WhatsApp intent on various devices
- Test code validation flow
- Test multi-user concurrent editing

## Security Considerations

1. **Code Uniqueness**: Ensure codes are truly random and unique
2. **Code Expiration**: Optional expiration for security
3. **One-time Use**: Codes can only be used once
4. **Access Control**: Firebase Security Rules to protect wedding data
5. **User Verification**: Phone or email verification before joining

## Firebase Security Rules Example

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /weddings/{weddingId} {
      allow read: if request.auth != null && (
        request.auth.uid == resource.data.ownerId ||
        request.auth.uid in resource.data.collaboratorIds
      );
      allow write: if request.auth != null &&
        request.auth.uid == resource.data.ownerId;
      
      match /inviteCodes/{code} {
        allow read: if request.auth != null;
        allow write: if request.auth != null &&
          request.auth.uid == get(/databases/$(database)/documents/weddings/$(weddingId)).data.ownerId;
      }
      
      match /collaborators/{userId} {
        allow read: if request.auth != null &&
          request.auth.uid == userId;
        allow write: if request.auth != null &&
          request.auth.uid == get(/databases/$(database)/documents/weddings/$(weddingId)).data.ownerId;
      }
    }
  }
}
```

## Cost Estimation (Firebase)

**Free Tier (Spark Plan)**:
- 50K reads/day
- 20K writes/day
- 1GB storage
- 10GB/month bandwidth

**Paid Tier (Blaze Plan)**:
- $0.06 per 100K reads
- $0.18 per 100K writes
- $0.18/GB storage
- $0.12/GB bandwidth

**Estimated Cost for 1000 active users**:
- ~$5-20/month depending on usage patterns

## Alternative: Custom Backend

If Firebase is too expensive:
- **Backend**: Node.js + Express + PostgreSQL
- **Real-time**: Socket.IO or Server-Sent Events
- **Hosting**: AWS/GCP/DigitalOcean ($5-50/month)
- **More control**: Custom business logic, data model
- **More work**: Infrastructure management, scaling

## Conclusion

The current implementation provides:
✅ Unique invite code generation
✅ WhatsApp message sharing
✅ Contact selection UI
✅ Local database storage
✅ Pending invite tracking

**Recommendation**: Start with current Room-based approach for MVP, then migrate to Firebase when scaling to multiple users per wedding.
