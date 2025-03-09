package school.exercise.shop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SendSMS extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 100;
    Button sendSMSButton;
    EditText phoneNumberInput;
    ArrayList<Item> itemsToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_sms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sms_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        itemsToSend = getIntent().getParcelableArrayListExtra("items");
        
        phoneNumberInput = findViewById(R.id.smsNumber);
        sendSMSButton = findViewById(R.id.sendSMSButton);
        
        sendSMSButton.setOnClickListener(v -> {
            if (checkSMSPermission()) {
                sendSMS();
            } else {
                requestSMSPermission();
            }
        });
    }

    private boolean checkSMSPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSMSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }

    private void sendSMS() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itemsToSend == null || itemsToSend.isEmpty()) {
            Toast.makeText(this, "No items selected to send", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            StringBuilder messageText = new StringBuilder("Your order:\n");
            int totalPrice = 0;

            for (Item item : itemsToSend) {
                messageText.append(item.getName())
                        .append(" - $")
                        .append(item.getPrice())
                        .append("\n");
                totalPrice += item.getPrice();
            }
            messageText.append("\nTotal Price: $").append(totalPrice);

            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(messageText.toString());
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}