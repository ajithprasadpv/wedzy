package io.example.wedzy.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.security.MessageDigest
import java.util.*

object RsvpLinkGenerator {
    
    private const val BASE_URL = "https://wedzy-rsvp.web.app"
    
    fun generateShareableLink(userId: String, eventId: Long = 0): String {
        val token = generateToken(userId, eventId)
        return "$BASE_URL/rsvp/$token"
    }
    
    fun generateToken(userId: String, eventId: Long = 0): String {
        val data = "$userId-$eventId-${System.currentTimeMillis()}"
        val bytes = MessageDigest.getInstance("SHA-256").digest(data.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16)
    }
    
    fun generateQRCode(link: String, size: Int = 512): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(link, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    fun generateGuestToken(guestId: Long, userId: String): String {
        val data = "$userId-$guestId-${UUID.randomUUID()}"
        val bytes = MessageDigest.getInstance("SHA-256").digest(data.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(12)
    }
    
    fun generateRsvpFormHtml(
        brideName: String,
        groomName: String,
        weddingDate: String,
        token: String,
        mealOptions: List<String> = listOf("Chicken", "Beef", "Fish", "Vegetarian", "Vegan")
    ): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RSVP - $brideName & $groomName</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }
        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 20px; padding: 40px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
        h1 { color: #667eea; text-align: center; margin-bottom: 10px; font-size: 2.5em; }
        .subtitle { text-align: center; color: #666; margin-bottom: 30px; font-size: 1.2em; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; color: #333; font-weight: 600; }
        input, select, textarea { width: 100%; padding: 12px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 16px; transition: border-color 0.3s; }
        input:focus, select:focus, textarea:focus { outline: none; border-color: #667eea; }
        .checkbox-group { display: flex; flex-wrap: wrap; gap: 10px; }
        .checkbox-item { display: flex; align-items: center; }
        .checkbox-item input { width: auto; margin-right: 8px; }
        button { width: 100%; padding: 15px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; font-size: 18px; font-weight: 600; cursor: pointer; transition: transform 0.2s; }
        button:hover { transform: translateY(-2px); }
        button:disabled { opacity: 0.6; cursor: not-allowed; }
        .success { background: #4caf50; color: white; padding: 20px; border-radius: 8px; text-align: center; display: none; }
        .error { background: #f44336; color: white; padding: 15px; border-radius: 8px; margin-bottom: 20px; display: none; }
    </style>
</head>
<body>
    <div class="container">
        <h1>$brideName & $groomName</h1>
        <p class="subtitle">Wedding Day: $weddingDate</p>
        
        <div id="error" class="error"></div>
        <div id="success" class="success">
            <h2>Thank you for your RSVP!</h2>
            <p>We can't wait to celebrate with you!</p>
        </div>
        
        <form id="rsvpForm">
            <div class="form-group">
                <label for="name">Full Name *</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="tel" id="phone" name="phone">
            </div>
            
            <div class="form-group">
                <label for="rsvp">Will you be attending? *</label>
                <select id="rsvp" name="rsvp" required>
                    <option value="">Please select...</option>
                    <option value="yes">Yes, I'll be there!</option>
                    <option value="no">Sorry, can't make it</option>
                </select>
            </div>
            
            <div id="attendingFields" style="display: none;">
                <div class="form-group">
                    <label for="meal">Meal Preference *</label>
                    <select id="meal" name="meal">
                        <option value="">Please select...</option>
                        ${mealOptions.joinToString("\n") { "<option value=\"${it.lowercase()}\">$it</option>" }}
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Dietary Restrictions</label>
                    <div class="checkbox-group">
                        <div class="checkbox-item"><input type="checkbox" name="dietary" value="vegetarian"> Vegetarian</div>
                        <div class="checkbox-item"><input type="checkbox" name="dietary" value="vegan"> Vegan</div>
                        <div class="checkbox-item"><input type="checkbox" name="dietary" value="gluten-free"> Gluten-Free</div>
                        <div class="checkbox-item"><input type="checkbox" name="dietary" value="dairy-free"> Dairy-Free</div>
                        <div class="checkbox-item"><input type="checkbox" name="dietary" value="nut-allergy"> Nut Allergy</div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="plusOne">Bringing a plus one?</label>
                    <select id="plusOne" name="plusOne">
                        <option value="no">No</option>
                        <option value="yes">Yes</option>
                    </select>
                </div>
                
                <div id="plusOneFields" style="display: none;">
                    <div class="form-group">
                        <label for="plusOneName">Plus One Name</label>
                        <input type="text" id="plusOneName" name="plusOneName">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="song">Song Request</label>
                    <input type="text" id="song" name="song" placeholder="What song gets you on the dance floor?">
                </div>
            </div>
            
            <div class="form-group">
                <label for="message">Message to the Couple</label>
                <textarea id="message" name="message" rows="4" placeholder="Share your well wishes..."></textarea>
            </div>
            
            <button type="submit" id="submitBtn">Submit RSVP</button>
        </form>
    </div>
    
    <script type="module">
        import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js';
        import { getDatabase, ref, push } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-database.js';
        
        const firebaseConfig = {
            apiKey: "YOUR_API_KEY",
            databaseURL: "YOUR_DATABASE_URL",
            projectId: "YOUR_PROJECT_ID"
        };
        
        const app = initializeApp(firebaseConfig);
        const database = getDatabase(app);
        
        const form = document.getElementById('rsvpForm');
        const rsvpSelect = document.getElementById('rsvp');
        const plusOneSelect = document.getElementById('plusOne');
        const attendingFields = document.getElementById('attendingFields');
        const plusOneFields = document.getElementById('plusOneFields');
        const submitBtn = document.getElementById('submitBtn');
        const errorDiv = document.getElementById('error');
        const successDiv = document.getElementById('success');
        
        rsvpSelect.addEventListener('change', (e) => {
            attendingFields.style.display = e.target.value === 'yes' ? 'block' : 'none';
        });
        
        plusOneSelect.addEventListener('change', (e) => {
            plusOneFields.style.display = e.target.value === 'yes' ? 'block' : 'none';
        });
        
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            submitBtn.disabled = true;
            submitBtn.textContent = 'Submitting...';
            errorDiv.style.display = 'none';
            
            try {
                const formData = new FormData(form);
                const dietary = Array.from(document.querySelectorAll('input[name="dietary"]:checked')).map(cb => cb.value);
                
                const rsvpData = {
                    name: formData.get('name'),
                    email: formData.get('email'),
                    phone: formData.get('phone'),
                    rsvp: formData.get('rsvp'),
                    meal: formData.get('meal'),
                    dietary: dietary,
                    plusOne: formData.get('plusOne') === 'yes',
                    plusOneName: formData.get('plusOneName'),
                    song: formData.get('song'),
                    message: formData.get('message'),
                    submittedAt: Date.now()
                };
                
                await push(ref(database, 'rsvps/$token'), rsvpData);
                
                form.style.display = 'none';
                successDiv.style.display = 'block';
            } catch (error) {
                errorDiv.textContent = 'Error submitting RSVP. Please try again.';
                errorDiv.style.display = 'block';
                submitBtn.disabled = false;
                submitBtn.textContent = 'Submit RSVP';
            }
        });
    </script>
</body>
</html>
        """.trimIndent()
    }
}
