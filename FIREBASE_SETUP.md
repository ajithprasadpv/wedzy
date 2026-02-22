# Firebase Setup Guide for Wedzy

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter project name: **"Wedzy"**
4. Enable/disable Google Analytics (recommended: enable)
5. Click **"Create project"**

## Step 2: Add Android App to Firebase

1. In Firebase Console, click **"Add app"** → Select **Android**
2. Enter package name: **`io.example.wedzy`**
3. Enter app nickname: **"Wedzy Android"**
4. (Optional) Enter SHA-1 for Google Sign-In (can add later)
5. Click **"Register app"**

## Step 3: Download Configuration File

1. Download **`google-services.json`**
2. Place it in: **`/Users/ajith/Documents/Wedzy/app/google-services.json`**

```
Wedzy/
├── app/
│   ├── google-services.json  ← PUT FILE HERE
│   ├── src/
│   └── build.gradle.kts
└── ...
```

## Step 4: Enable Firebase Services

### Authentication
1. In Firebase Console → **Authentication** → **Get started**
2. Go to **Sign-in method** tab
3. Enable **Email/Password**
4. (Optional) Enable **Google** sign-in

### Firestore Database
1. In Firebase Console → **Firestore Database** → **Create database**
2. Select **Start in test mode** (for development)
3. Choose location closest to your users (e.g., `asia-south1` for India)
4. Click **Create**

## Step 5: Configure Firestore Security Rules

Go to **Firestore Database** → **Rules** tab and paste:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Weddings collection
    match /weddings/{weddingId} {
      // Allow read if user is owner or collaborator
      allow read: if request.auth != null && (
        request.auth.uid == resource.data.ownerId ||
        request.auth.uid in resource.data.collaboratorIds
      );
      
      // Allow create for authenticated users
      allow create: if request.auth != null;
      
      // Allow update/delete only for owner
      allow update, delete: if request.auth != null &&
        request.auth.uid == resource.data.ownerId;
      
      // Invite codes subcollection
      match /inviteCodes/{code} {
        // Anyone authenticated can read (for validation)
        allow read: if request.auth != null;
        
        // Only owner can create invite codes
        allow create: if request.auth != null &&
          request.auth.uid == get(/databases/$(database)/documents/weddings/$(weddingId)).data.ownerId;
        
        // Allow update when joining (marking code as used)
        allow update: if request.auth != null;
      }
      
      // Collaborators subcollection
      match /collaborators/{collabId} {
        allow read: if request.auth != null &&
          request.auth.uid in get(/databases/$(database)/documents/weddings/$(weddingId)).data.collaboratorIds;
        allow write: if request.auth != null &&
          request.auth.uid == get(/databases/$(database)/documents/weddings/$(weddingId)).data.ownerId;
      }
    }
  }
}
```

Click **Publish** to save.

## Step 6: Sync Gradle

After adding `google-services.json`:
1. In Android Studio, click **"Sync Now"** in the yellow banner
2. Or go to **File → Sync Project with Gradle Files**

## Step 7: Configure Google Sign-In Web Client ID

1. Open your downloaded `google-services.json`
2. Find the `oauth_client` section with `"client_type": 3`
3. Copy the `client_id` value (looks like `xxxx.apps.googleusercontent.com`)
4. Open `app/src/main/res/values/strings.xml`
5. Replace `YOUR_WEB_CLIENT_ID` with your actual client ID:

```xml
<string name="default_web_client_id" translatable="false">123456789-abcdef.apps.googleusercontent.com</string>
```

**Note**: If you don't see `client_type: 3` in your `google-services.json`, you need to:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project
3. Go to **APIs & Services** → **Credentials**
4. Create an **OAuth 2.0 Client ID** of type **Web application**
5. Copy the Client ID and paste it in `strings.xml`

## Step 8: Test the Integration

1. Build and run the app
2. Try **"Continue with Google"** on the Auth screen
3. Go to **Team Collaboration** screen
4. Try **"Select from contacts"** → Should generate invite codes
5. Check Firebase Console → **Firestore** to see data

## Troubleshooting

### Build Error: "google-services.json not found"
- Ensure file is in `app/` folder (not `app/src/`)
- File name must be exactly `google-services.json`

### Authentication Error
- Check Firebase Console → Authentication → Users tab
- Ensure Email/Password provider is enabled

### Firestore Permission Denied
- Check security rules are published
- Ensure user is authenticated before accessing data

### SHA-1 for Google Sign-In (Optional)

To get SHA-1 fingerprint:
```bash
cd /Users/ajith/Documents/Wedzy
./gradlew signingReport
```

Add the SHA-1 to Firebase Console → Project Settings → Your apps → Add fingerprint

## Data Structure in Firestore

```
weddings/
  └── {weddingId}/
      ├── brideName: "Sarah"
      ├── groomName: "John"
      ├── weddingDate: 1735689600000
      ├── ownerId: "user123"
      ├── collaboratorIds: ["user123", "user456"]
      └── inviteCodes/
          └── {code}/
              ├── code: "ABC123XYZ0"
              ├── invitedName: "Mom"
              ├── invitedPhone: "+1234567890"
              ├── role: "FAMILY"
              ├── isUsed: false
              └── createdAt: timestamp

users/
  └── {userId}/
      ├── displayName: "Sarah Smith"
      ├── email: "sarah@example.com"
      ├── ownedWeddings: ["weddingId1"]
      └── joinedWeddings: ["weddingId1", "weddingId2"]
```

## Next Steps

After setup:
1. ✅ Users can sign up/sign in
2. ✅ Wedding data syncs to cloud
3. ✅ Invite codes are validated across devices
4. ✅ Multiple users can collaborate on same wedding

## Cost Estimate (Firebase Free Tier)

- **Authentication**: Unlimited users (free)
- **Firestore**: 
  - 50K reads/day
  - 20K writes/day
  - 1GB storage
- **Sufficient for**: ~500-1000 active users

Upgrade to Blaze plan when scaling beyond free tier (~$5-20/month).
