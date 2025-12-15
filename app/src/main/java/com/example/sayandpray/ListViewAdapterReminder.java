package com.example.sayandpray;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sayandpray.model.Product;
import com.example.sayandpray.model.Reminder;

import java.util.List;

public class ListViewAdapterReminder extends ArrayAdapter<Reminder> {
    DatabaseHelper db = DatabaseHelper.getInstance(getContext());
    private Context context;
    private int resource;
    private List<Reminder> reminderList;


    public ListViewAdapterReminder(@NonNull Context context, int resource, @NonNull List<Reminder> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.reminderList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reminder remind = getItem(position);
        int reminderId = remind.getIdReminder();

        int jamRemind = getItem(position).getJamReminder();
        int menitRemind = getItem(position).getMenitReminder();
        int idDoa = getItem(position).getIdDoa();

        Product product = db.getDoaByID(idDoa);
        String namaDoa = product.getNamaDoa();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource,parent,false);

        TextView txtNama = (TextView) convertView.findViewById(R.id.judulLbl);
        TextView txtJam = (TextView) convertView.findViewById(R.id.jamLbl);
        TextView txtMenit = (TextView) convertView.findViewById(R.id.menitLbl);
        View deleteBtn = convertView.findViewById(R.id.btnDeleteReminder);

        txtNama.setText(namaDoa);
        txtJam.setText(String.format("%02d", jamRemind));
        txtMenit.setText(String.format("%02d", menitRemind));

        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Reminder")
                    .setMessage("Yakin ingin menghapus reminder ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        boolean berhasil = db.deleteReminder(reminderId);
                        if (berhasil) {
                            reminderList.remove(position); // pastikan reminderList adalah list yang kamu pakai
                            notifyDataSetChanged();
                            Toast.makeText(context,  "Reminder dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Gagal menghapus reminder", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        return convertView;
    }
}
