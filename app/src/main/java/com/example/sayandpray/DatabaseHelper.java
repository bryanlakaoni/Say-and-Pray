package com.example.sayandpray;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


import com.example.sayandpray.model.Product;
import com.example.sayandpray.model.Reminder;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private DatabaseHelper(@Nullable Context context) {
        super(context, "bukuDoa", null, 3);
    }

    // Method untuk mendapatkan instance
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE doa (" +
                "doaId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "detail TEXT," +
                "favorite INTEGER)");

        db.execSQL("CREATE TABLE reminders (" +
                "reminderId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "doaId INTEGER," +
                "hour INTEGER," +
                "minute INTEGER," +
                "FOREIGN KEY(doaId) REFERENCES doa(doaId))");
        insertDefaultPrayers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists doa");
        db.execSQL("drop table if exists reminders");
        onCreate(db);
    }


    //DOA-----------------------------------------
    public boolean insertDoa(String name, String detail) {
        boolean kembalian = false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",name);
        contentValues.put("Detail",detail);
        contentValues.put("favorite", 0);

        long hasil = db.insert("doa", null, contentValues);
        if(hasil!=-1) kembalian = true;
        return kembalian;
    }

    public Cursor getDataDoa() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from doa", null);
        return cursor;
    }

    public ArrayList<Product> getListDoa() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT doaId, name, detail, favorite FROM doa", null);

        if (cursor.moveToFirst()) {
            do {
                int idIdx = cursor.getColumnIndex("doaId");
                int nameIdx = cursor.getColumnIndex("name");
                int detailIdx = cursor.getColumnIndex("detail");
                int getFav = cursor.getColumnIndex("favorite");

                if (idIdx != -1 && nameIdx != -1 && detailIdx != -1) {

                    int doaId = cursor.getInt(idIdx);
                    String name = cursor.getString(nameIdx);
                    String detail = cursor.getString(detailIdx);
                    boolean isFav = cursor.getInt(getFav) == 1;

                    Product product = new Product(doaId, name, detail, isFav);

                    productList.add(product);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    public ArrayList<Product> getListDoaFavorit(){
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT doaId, name, detail, favorite FROM doa WHERE favorite = 1", null);

        if (cursor.moveToFirst()) {
            do {
                int idIdx = cursor.getColumnIndex("doaId");
                int nameIdx = cursor.getColumnIndex("name");
                int detailIdx = cursor.getColumnIndex("detail");
                int getFav = cursor.getColumnIndex("favorite");

                if (idIdx != -1 && nameIdx != -1 && detailIdx != -1) {

                    int doaId = cursor.getInt(idIdx);
                    String name = cursor.getString(nameIdx);
                    String detail = cursor.getString(detailIdx);
                    boolean isFav = cursor.getInt(getFav) == 1;

                    Product product = new Product(doaId, name, detail, isFav);

                    productList.add(product);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    public boolean updateFavorit(int ID, boolean fav) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorite", fav ? 1 : 0);

        int row = db.update(
                "doa",
                contentValues,
                "doaId = ?",
                new String[]{String.valueOf(ID)}
        );

        return row > 0;
    }

    public Product getDoaByID(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT doaId, name, detail, favorite FROM doa WHERE doaId = ?",
                new String[]{String.valueOf(id)}
        );

        Product product = null;
        if (cursor.moveToFirst()) {
            int doaId = cursor.getInt(cursor.getColumnIndexOrThrow("doaId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow("detail"));
            boolean isFav = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1;

            product = new Product(doaId, name, detail, isFav);
        }

        cursor.close();
        return product;
    }

    //REMINDER-----------------------------------------
    public long insertReminder(int doaId, int hour, int minute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("doaId",doaId);
        contentValues.put("hour",hour);
        contentValues.put("minute", minute);

        long hasil = db.insert("reminders", null, contentValues);
        return hasil;
    }

//    public Cursor getDataDoa() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("Select * from doa", null);
//        return cursor;
//    }

    public ArrayList<Reminder> getListReminder() {
        ArrayList<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT reminderId, doaId, hour, minute FROM reminders", null);

        if (cursor.moveToFirst()) {
            do {
                int idRemIdx = cursor.getColumnIndex("reminderId");
                int idDoaIdx = cursor.getColumnIndex("doaId");
                int hourIdx = cursor.getColumnIndex("hour");
                int minuteIdx = cursor.getColumnIndex("minute");

                if (idRemIdx != -1 && idDoaIdx != -1 && hourIdx != -1 && minuteIdx != -1) {

                    int reminderId = cursor.getInt(idRemIdx);
                    int doaId = cursor.getInt(idDoaIdx);
                    int hour = cursor.getInt(hourIdx);
                    int minute = cursor.getInt(minuteIdx);

                    Reminder reminder = new Reminder(reminderId, doaId, hour, minute);

                    reminderList.add(reminder);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return reminderList;
    }

    public boolean deleteReminder(int idToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("reminders", "reminderId = ?", new String[]{String.valueOf(idToDelete)});
        return rows > 0;
    }

    public boolean updateReminder(int ID, int hour, int minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hour", hour);
        contentValues.put("minute", minute);

        int row = db.update(
                "reminders",
                contentValues,
                "reminderId = ?",
                new String[]{String.valueOf(ID)}
        );

        return row > 0;
    }

    // HAPUS DOA N REMINDER -------------------------
    public boolean deleteDoa(int doaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminders", "doaId = ?", new String[]{String.valueOf(doaId)});
        long result = db.delete("doa", "doaId = ?", new String[]{String.valueOf(doaId)});
        return result != -1;
    }

    // ISI DOA BAWAAN APLIKASI -----------------------
    private void insertDefaultPrayers(SQLiteDatabase db) {

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Bapa Kami', " +
                "'Bapa kami yang ada di surga, Dimuliakanlah nama-Mu. Datanglah kerajaan-Mu. Jadilah kehendak-Mu di atas bumi seperti di dalam surga. Berilah kami rezeki pada hari ini, dan ampunilah kesalahan kami, seperti kami pun mengampuni yang bersalah kepada kami. Dan janganlah masukkan kami ke dalam pencobaan, tetapi bebaskanlah kami dari yang jahat. Amin.', " +
                "0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Salam Maria', " +
                "'Salam Maria, penuh rahmat, Tuhan sertamu, terpujilah engkau di antara wanita, dan terpujilah buah tubuhmu, Yesus. Santa Maria, bunda Allah, doakanlah kami yang berdosa ini sekarang dan waktu kami mati. Amin.', " +
                "0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Kemuliaan', " +
                "'Kemuliaan kepada Bapa dan Putra dan Roh Kudus, seperti pada permulaan, sekarang, selalu, dan sepanjang segala abad. Amin.', " +
                "0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Doa Tobat', " +
                "'Allah yang maharahim, aku menyesal atas dosa-dosaku. Aku sungguh patut Engkau hukum, terutama karena aku telah tidak setia kepada Engkau yang maha pengasih dan mahabaik bagiku. Aku benci akan segala dosaku, dan aku berjanji dengan pertolongan rahmat-Mu hendak memperbaiki hidupku dan tidak akan berbuat dosa lagi. Allah yang mahamurah, ampunilah aku, orang berdosa. Amin.', 0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Ratu Surga (Masa Paskah)', " +
                "'Ratu Surga, bersukacitalah, haleluya.\nSebab Ia yang sudi kau kandung, haleluya.\nTelah bangkit seperti yang disabdakan-Nya, haleluya.\nDoakanlah kami pada Allah, haleluya.\n\nBersukacitalah dan bergembiralah, Perawan Maria, haleluya.\nSebab Tuhan sungguh telah bangkit, haleluya.\n\nMarilah berdoa: Ya Allah, Engkau telah menggembirakan dunia dengan kebangkitan Putra-Mu, Tuhan kami Yesus Kristus. Kami mohon, perkenankanlah kami bersukacita dalam kehidupan kekal bersama Bunda-Nya, Perawan Maria. Demi Kristus, Pengantara kami. Amin.', 0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Syahadat Para Rasul', " +
                "'Aku percaya akan Allah, Bapa yang mahakuasa, Pencipta langit dan bumi. Dan akan Yesus Kristus, Putra-Nya yang tunggal, Tuhan kita. Yang dikandung dari Roh Kudus, dilahirkan oleh Perawan Maria. Yang menderita sengsara dalam pemerintahan Pontius Pilatus, disalibkan, wafat, dan dimakamkan. Yang turun ke tempat penantian, pada hari ketiga bangkit dari antara orang mati. Yang naik ke surga, duduk di sebelah kanan Allah Bapa yang mahakuasa. Dari situ Ia akan datang mengadili orang yang hidup dan yang mati. Aku percaya akan Roh Kudus, Gereja Katolik yang kudus, persekutuan para kudus, pengampunan dosa, kebangkitan badan, kehidupan kekal. Amin.', 0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Doa Sebelum Makan', " +
                "'Ya Tuhan, berkatilah kami dan anugerah yang akan kami santap ini dari kemurahan-Mu. Melalui Kristus Tuhan kami. Amin.', " +
                "0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Malaikat Tuhan (Angelus)', " +
                "'Maria diberi kabar oleh malaikat Tuhan, Bahwa ia akan mengandung dari Roh Kudus. Salam Maria... Aku ini hamba Tuhan, Terjadilah padaku menurut perkataanmu. Salam Maria... Sabda sudah menjadi daging, Dan tinggal di antara kita. Salam Maria... Doakanlah kami, ya Santa Bunda Allah, Supaya kami dapat menikmati janji Kristus. Amin.', " +
                "0)");

        db.execSQL("INSERT INTO doa (name, detail, favorite) VALUES (" +
                "'Malaikat Pelindung', " +
                "'Malaikat Allah, engkau yang diserahi tugas oleh kemurahan Tuhan untuk melindungiku; terangilah, lindungilah, bimbinglah, dan hantarlah aku pada hari ini. Amin.', 0)");
    }
}
