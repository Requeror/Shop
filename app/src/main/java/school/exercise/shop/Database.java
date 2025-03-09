package school.exercise.shop;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_BUYER = "buyer";
    private Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_IMAGE + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_BUYER + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addItem(String name, int price, int imageResourceId, String date, String buyer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IMAGE, imageResourceId);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_BUYER, buyer);

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_NAME + " LIMIT 10 OFFSET ?";
        int offset = 0;
        boolean hasMore = true;

        while (hasMore) {
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(offset)});
            
            if (cursor.moveToFirst()) {
                do {
                    try {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                        int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                        int imageResourceId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                        String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                        String buyer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUYER));

                        Item item = new Item(name, price, imageResourceId, date, buyer);
                        items.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
                
                hasMore = cursor.getCount() == 10;
                offset += 10;
            } else {
                hasMore = false;
            }
            
            cursor.close();
        }
        
        db.close();
        return items;
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
