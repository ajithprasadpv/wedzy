# 📦 Build AAB File - Simple Instructions

## ✅ What I've Done For You (Backend Changes)

1. ✅ Updated package name to: `com.ajith.wedzy`
2. ✅ Added automatic signing configuration
3. ✅ Enabled ProGuard for app optimization
4. ✅ Created keystore.properties template
5. ✅ Updated .gitignore for security

---

## 🎯 YOUR TASKS (Only 2 Steps!)

### **TASK 1: Create Keystore (One-time only)**

Open Terminal in Android Studio (bottom panel) and run:

```bash
cd /Users/ajith/Documents/Wedzy

keytool -genkey -v -keystore wedzy-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias wedzy-key
```

**You'll be asked for:**
- Keystore password: [Create a strong password]
- Re-enter password: [Same password]
- Key password: [Press Enter to use same password OR create different one]
- First and Last Name: Ajith (or your full name)
- Organizational Unit: [Press Enter to skip]
- Organization: [Press Enter to skip]
- City: [Your city or press Enter]
- State: [Your state or press Enter]
- Country Code: IN [or your country code]
- Is this correct? Type: **yes**

**⚠️ SAVE YOUR PASSWORD(S)** - Write them down somewhere safe!

---

### **TASK 2: Create keystore.properties File**

1. Copy the template file:
```bash
cp keystore.properties.template keystore.properties
```

2. Open `keystore.properties` in Android Studio

3. Replace the placeholders:
```properties
storePassword=YOUR_ACTUAL_PASSWORD_HERE
keyPassword=YOUR_ACTUAL_PASSWORD_HERE
keyAlias=wedzy-key
storeFile=wedzy-release-key.jks
```

4. Save the file

---

## 🚀 BUILD THE AAB

### **Method 1: Using Android Studio (Recommended)**

1. Click **Build** → **Generate Signed Bundle / APK**
2. Select **Android App Bundle**
3. Click **Next**
4. It will automatically use your keystore.properties
5. Click **Finish**

**Output:** `/Users/ajith/Documents/Wedzy/app/release/app-release.aab`

### **Method 2: Using Terminal**

```bash
./gradlew bundleRelease
```

**Output:** `/Users/ajith/Documents/Wedzy/app/build/outputs/bundle/release/app-release.aab`

---

## 📱 IMPORTANT: Firebase Setup (Before Uploading to Play Store)

After creating the keystore, you need to add its SHA-1 to Firebase:

### **Get SHA-1:**
```bash
keytool -list -v -keystore wedzy-release-key.jks -alias wedzy-key
```

Copy the SHA1 line (looks like: `7B:2B:7A:58:...`)

### **Add to Firebase:**
1. Go to: https://console.firebase.google.com/
2. Select project: **wedzy-13ad8**
3. Click ⚙️ **Project Settings**
4. Scroll to **Your apps** → Click on Android app
5. Click **Add fingerprint**
6. Paste SHA-1
7. Click **Save**
8. **Download new google-services.json**
9. Replace file in: `/Users/ajith/Documents/Wedzy/app/google-services.json`
10. **Rebuild the AAB** (important!)

---

## ✅ Final Checklist

- [ ] Keystore created (`wedzy-release-key.jks`)
- [ ] `keystore.properties` file created with passwords
- [ ] SHA-1 added to Firebase Console
- [ ] New `google-services.json` downloaded and replaced
- [ ] AAB built successfully
- [ ] AAB file ready for upload

---

## 🎉 Upload to Google Play

1. Go to Google Play Console
2. Create new app or select existing
3. Go to **Release** → **Production** → **Create new release**
4. Upload `app-release.aab`
5. Fill in release notes
6. Submit for review

---

**That's it! You're done!** 🎊
