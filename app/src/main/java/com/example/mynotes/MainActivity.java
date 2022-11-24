package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mynotes.database.Note;
import com.example.mynotes.database.NoteDao;
import com.example.mynotes.database.NoteRoomDatabase;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private NoteDao mNotesDao;
    private ExecutorService executorService;
    private ListView listView;
    private Button btnAddNote, btnEditNote;
    private EditText editTitle, editDesc, editDate;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_note);
        btnAddNote = findViewById(R.id.btn_add_note);
        btnEditNote = findViewById(R.id.btn_update_note);
        editDate = findViewById(R.id.edt_date);
        editDesc = findViewById(R.id.edt_description);
        editTitle = findViewById(R.id.edt_title);

        // untuk menjalankan di background
        executorService = Executors.newSingleThreadExecutor();

        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNotesDao = db.notedao();

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String desc = editDesc.getText().toString();
                String date = editDate.getText().toString();
                insertData(new Note(title, desc, date));
                setEmpetyField();
            }
        });

        btnEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String desc = editDesc.getText().toString();
                String date = editDate.getText().toString();
                updateData(new Note(id, title, desc, date));
                id = 0;
                setEmpetyField();
                getAllNote();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note item = (Note) adapterView.getAdapter().getItem(i);
                id = item.getId();
                editTitle.setText(item.getTitle());
                editDesc.setText(item.getDescription());
                editDate.setText(item.getDate());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note item = (Note) adapterView.getAdapter().getItem(i);
                deleteData(item);
                return true;
            }
        });

        getAllNote();
    }

    private void setEmpetyField(){
        editTitle.setText("");
        editDesc.setText("");
        editDate.setText("");
    }

    // function mendapatkan semua data notes di database
    private void getAllNote(){
        mNotesDao.getAllNote().observe(this, notes -> {
            ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this,
                    android.R.layout.simple_expandable_list_item_1, notes);
            listView.setAdapter(adapter);
        });
    }

    // function insert data ke room
    private void insertData(Note note){
        executorService.execute(() -> mNotesDao.insert(note));
    }

    // function update data
    private void updateData(Note note){
        executorService.execute(() -> mNotesDao.update(note));
    }

    // function delete data
    private void deleteData(Note note){
        executorService.execute(() -> mNotesDao.delete(note));
    }
}