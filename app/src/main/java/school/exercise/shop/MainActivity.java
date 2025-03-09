package school.exercise.shop;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerMouse;
    Spinner spinnerComputer;
    Spinner spinnerKeyboard;
    Spinner spinnerMonitor;
    ImageView mouseImage;
    ImageView computerImage;
    ImageView monitorImage;
    ImageView keyboardImage;
    int[] mousePrices;
    int[] computerPrices;
    int[] monitorPrices;
    int[] keyboardPrices;
    Button price;
    Button order;
    SeekBar amountSeek;
    Button add;
    Button sub;
    TextView amountText;
    CharSequence amountString;
    CharSequence priceString;
    TextView priceView;
    int currentSeek;
    CheckBox checkMice;
    CheckBox checkKeyboard;
    CheckBox checkMonitor;
    Database db;
    Item mouseItem;
    Item computerItem;
    Item monitorItem;
    Item keyboardItem;
    ArrayList<Item> items;
    Intent intent;
    EditText username;

    private static final String PREFS_NAME = "ShopPreferences";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_MOUSE_POSITION = "mouse_position";
    private static final String PREF_COMPUTER_POSITION = "computer_position";
    private static final String PREF_KEYBOARD_POSITION = "keyboard_position";
    private static final String PREF_MONITOR_POSITION = "monitor_position";
    private static final String PREF_AMOUNT = "amount";
    private static final String PREF_MOUSE_CHECKED = "mouse_checked";
    private static final String PREF_KEYBOARD_CHECKED = "keyboard_checked";
    private static final String PREF_MONITOR_CHECKED = "monitor_checked";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.orderList){
            intent = new Intent(this, OrderList.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.sendSMS){
            int totalPrice = 0;
            
            if(computerItem != null) {
                totalPrice += computerItem.getPrice() * currentSeek;
            }
            
            if(checkMice.isChecked() && mouseItem != null) {
                totalPrice += mouseItem.getPrice();
            }
            if(checkKeyboard.isChecked() && keyboardItem != null) {
                totalPrice += keyboardItem.getPrice();
            }
            if(checkMonitor.isChecked() && monitorItem != null) {
                totalPrice += monitorItem.getPrice();
            }

            if(totalPrice <= 0) {
                Toast.makeText(this, "Please select items and set amount greater than 0", Toast.LENGTH_SHORT).show();
                return true;
            }

            ArrayList<Item> currentItems = new ArrayList<>();
            
            if(computerItem != null) {
                Item computerWithQuantity = new Item(
                    computerItem.getName(),
                    computerItem.getPrice() * currentSeek,
                    computerItem.getPict(),
                    LocalDateTime.now().toString(),
                    username.getText().toString()
                );
                currentItems.add(computerWithQuantity);
            }
            
            if(checkMice.isChecked() && mouseItem != null) {
                currentItems.add(mouseItem);
            }
            if(checkKeyboard.isChecked() && keyboardItem != null) {
                currentItems.add(keyboardItem);
            }
            if(checkMonitor.isChecked() && monitorItem != null) {
                currentItems.add(monitorItem);
            }

            intent = new Intent(this, SendSMS.class);
            intent.putParcelableArrayListExtra("items", currentItems);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.share){
            shareOrder();
        }
        if(item.getItemId() == R.id.saveSettings){
            saveUserSettings();
        }
        if(item.getItemId() == R.id.about){
            showAboutInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(findViewById(R.id.toolbar));

        AppShortcutManager.createShortcuts(this);

        String action = getIntent().getAction();
        if (action != null) {
            switch (action) {
                case "school.exercise.shop.SHARE_ORDER":
                    shareOrder();
                    break;
                case "school.exercise.shop.CALCULATE_PRICE":
                    calculatePrice();
                    break;
            }
        }

        price = findViewById(R.id.calculatePrice);
        order = findViewById(R.id.order);
        add = findViewById(R.id.add);
        sub = findViewById(R.id.sub);
        amountSeek = findViewById(R.id.seekBar);
        amountSeek.setMin(0);
        amountText = findViewById(R.id.amount);
        priceView = findViewById(R.id.price);
        amountString = getResources().getText(R.string.amount);
        priceString = getResources().getString(R.string.price);
        username = findViewById(R.id.username);

        checkMice = findViewById(R.id.checkMouse);
        checkKeyboard = findViewById(R.id.checkKeyboard);
        checkMonitor = findViewById(R.id.checkMonitor);
        db = new Database(this);
        items = new ArrayList<>();

        currentSeek = 0;
        amountSeek.setProgress(0);
        amountText.setText(amountString + " 0");
        
        spinnerHandler();
        loadUserSettings();
        calculatePrice();

        amountSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentSeek = amountSeek.getProgress();
                amountText.setText(amountString + " " + currentSeek);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        add.setOnClickListener(v -> {
            amountSeek.setProgress(amountSeek.getProgress() + 1);
            amountText.setText(amountString + " " + amountSeek.getProgress());
        });
        sub.setOnClickListener(v -> {
            amountSeek.setProgress(amountSeek.getProgress() - 1);
            amountText.setText(amountString + " " + amountSeek.getProgress());
        });
        price.setOnClickListener(v -> {
            calculatePrice();
        });
        order.setOnClickListener(v -> {
            items.clear();

            if(computerItem != null) {
                computerItem.setPrice(computerItem.getPrice() * currentSeek);
                items.add(computerItem);
            }

            if(checkMice.isChecked() && mouseItem != null && mouseItem.getPrice() != 0) {
                items.add(mouseItem);
            }
            if(checkKeyboard.isChecked() && keyboardItem != null && keyboardItem.getPrice() != 0) {
                items.add(keyboardItem);
            }
            if(checkMonitor.isChecked() && monitorItem != null && monitorItem.getPrice() != 0) {
                items.add(monitorItem);
            }

            if(items.isEmpty()) {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show();
                return;
            }

            if(username.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            order(items);
        });
    }
    public void spinnerHandler(){
        spinnerMouse = findViewById(R.id.spinnerMouse);
        spinnerComputer = findViewById(R.id.spinnerComputer);
        spinnerKeyboard = findViewById(R.id.spinnerKeyboard);
        spinnerMonitor = findViewById(R.id.spinnerMonitor);
        mouseImage = findViewById(R.id.mouseImage);
        computerImage = findViewById(R.id.computerImage);
        keyboardImage = findViewById(R.id.keyboardImage);
        monitorImage = findViewById(R.id.monitorImage);
        int[] mouseImages = {R.drawable.mouse1, R.drawable.mouse2, R.drawable.mouse3};
        int[] computerImages = {R.drawable.budgetcomputer, R.drawable.gamingcomputer, R.drawable.expensivecomputer};
        int[] keyboardImages = {R.drawable.k1, R.drawable.k2, R.drawable.k3};
        int[] monitorImages = {R.drawable.m1, R.drawable.m2, R.drawable.m3};

        mousePrices = new int[] {200, 150, 100};
        computerPrices = new int[] {1500, 4000, 10000};
        keyboardPrices = new int[] {300, 350, 250};
        monitorPrices = new int[] {1000, 700, 500};



        String[] itemsMouse = getResources().getStringArray(R.array.spinnerMouse);
        String[] itemsComputer = getResources().getStringArray(R.array.spinnerComputer);
        String[] itemsKeyboard = getResources().getStringArray(R.array.spinnerKeyboard);
        String[] itemsMonitor = getResources().getStringArray(R.array.spinnerMonitor);

        ArrayAdapter<String> adapterMouse = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsMouse);
        adapterMouse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterComputer = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsComputer);
        adapterComputer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterKeyboard = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsKeyboard);
        adapterKeyboard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterMonitor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsMonitor);
        adapterMonitor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMouse.setAdapter(adapterMouse);
        spinnerComputer.setAdapter(adapterComputer);
        spinnerKeyboard.setAdapter(adapterKeyboard);
        spinnerMonitor.setAdapter(adapterMonitor);

        spinnerMouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mouseImage.setImageResource(mouseImages[position]);
                mouseItem = new Item(itemsMouse[position], mousePrices[position], mouseImages[position], LocalDateTime.now().toString(), username.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerComputer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                computerImage.setImageResource(computerImages[position]);
                computerItem = new Item(itemsComputer[position], computerPrices[position], computerImages[position], LocalDateTime.now().toString(), username.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerMonitor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monitorImage.setImageResource(monitorImages[position]);
                monitorItem = new Item(itemsMonitor[position], monitorPrices[position], monitorImages[position], LocalDateTime.now().toString(), username.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerKeyboard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyboardImage.setImageResource(keyboardImages[position]);
                keyboardItem = new Item(itemsKeyboard[position], keyboardPrices[position], keyboardImages[position], LocalDateTime.now().toString(), username.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void calculatePrice(){
        int mousePrice = mouseItem != null ? mousePrices[spinnerMouse.getSelectedItemPosition()] : 0;
        int keyboardPrice = keyboardItem != null ? keyboardPrices[spinnerKeyboard.getSelectedItemPosition()] : 0;
        int monitorPrice = monitorItem != null ? monitorPrices[spinnerMonitor.getSelectedItemPosition()] : 0;
        int computerPrice = computerItem != null ? computerPrices[spinnerComputer.getSelectedItemPosition()] : 0;

        if(!checkMice.isChecked()){
            mousePrice = 0;
        }
        if(!checkKeyboard.isChecked()){
            keyboardPrice = 0;
        }
        if(!checkMonitor.isChecked()){
            monitorPrice = 0;
        }

        int order_price = mousePrice + (computerPrice * currentSeek) + keyboardPrice + monitorPrice;
        String sOrder_price = order_price + "$";
        priceView.setText(String.format("%s%s", priceString, sOrder_price));
        if(mouseItem != null) mouseItem.setPrice(mousePrice);
        if(keyboardItem != null) keyboardItem.setPrice(keyboardPrice);
        if(monitorItem != null) monitorItem.setPrice(monitorPrice);
        if(computerItem != null) computerItem.setPrice(computerPrice);
    }
    public void order(ArrayList<Item> items){
        boolean success = true;
        try {
            String buyerName = username.getText().toString().trim();
            String currentTime = LocalDateTime.now().toString();
            
            for(Item item : items) {
                item.setBuyer(buyerName);
                item.setDate(currentTime);
            }

            for(Item item : items){
                long result = db.addItem(item.getName(), item.getPrice(), item.getPict(), item.getDate(), item.getBuyer());
                if(result == -1) {
                    success = false;
                    Toast.makeText(this, "Failed to save order", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            
            if(success) {
                Toast.makeText(this, "Order saved successfully", Toast.LENGTH_SHORT).show();
                items.clear();

                username.setText("");
                checkMice.setChecked(false);
                checkKeyboard.setChecked(false);
                checkMonitor.setChecked(false);
                amountSeek.setProgress(0);
                currentSeek = 0;
                amountText.setText(amountString + " 0");

                spinnerMouse.setSelection(0);
                spinnerComputer.setSelection(0);
                spinnerKeyboard.setSelection(0);
                spinnerMonitor.setSelection(0);

                calculatePrice();
            }
        } catch(Exception e) {
            success = false;
            Toast.makeText(this, "Error saving order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareOrder() {
        ArrayList<Item> orderItems = db.getAllItems();
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "No orders to share", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Order Details:\n\n");
            
            for (Item item : orderItems) {
                emailBody.append("Item: ").append(item.getName())
                        .append("\nPrice: $").append(item.getPrice())
                        .append("\nBuyer: ").append(item.getBuyer())
                        .append("\nDate: ").append(item.getDate())
                        .append("\n\n");
            }

            runOnUiThread(() -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Shop Order Details");
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void saveUserSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(PREF_USERNAME, username.getText().toString());

        editor.putInt(PREF_MOUSE_POSITION, spinnerMouse.getSelectedItemPosition());
        editor.putInt(PREF_COMPUTER_POSITION, spinnerComputer.getSelectedItemPosition());
        editor.putInt(PREF_KEYBOARD_POSITION, spinnerKeyboard.getSelectedItemPosition());
        editor.putInt(PREF_MONITOR_POSITION, spinnerMonitor.getSelectedItemPosition());

        editor.putInt(PREF_AMOUNT, amountSeek.getProgress());

        editor.putBoolean(PREF_MOUSE_CHECKED, checkMice.isChecked());
        editor.putBoolean(PREF_KEYBOARD_CHECKED, checkKeyboard.isChecked());
        editor.putBoolean(PREF_MONITOR_CHECKED, checkMonitor.isChecked());

        editor.apply();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }
    private void loadUserSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        username.setText(settings.getString(PREF_USERNAME, ""));
        spinnerMouse.setSelection(settings.getInt(PREF_MOUSE_POSITION, 0));
        spinnerComputer.setSelection(settings.getInt(PREF_COMPUTER_POSITION, 0));
        spinnerKeyboard.setSelection(settings.getInt(PREF_KEYBOARD_POSITION, 0));
        spinnerMonitor.setSelection(settings.getInt(PREF_MONITOR_POSITION, 0));
        amountSeek.setProgress(0);
        currentSeek = 0;
        amountText.setText(amountString + " 0");
        checkMice.setChecked(settings.getBoolean(PREF_MOUSE_CHECKED, false));
        checkKeyboard.setChecked(settings.getBoolean(PREF_KEYBOARD_CHECKED, false));
        checkMonitor.setChecked(settings.getBoolean(PREF_MONITOR_CHECKED, false));
        calculatePrice();
    }
    private void showAboutInfo() {
        Toast.makeText(this, "Application made by Paweł Sobiesiak", Toast.LENGTH_LONG).show();
    }
}