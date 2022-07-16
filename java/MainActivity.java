package com;

import static com.VariablesConfig.ALL_COUNT_WORDS;
import static com.VariablesConfig.COLUMN_WIDTH;
import static com.VariablesConfig.DB_NAME;
import static com.VariablesConfig.DB_PATH;
import static com.VariablesConfig.SQL_QUERY_LIKE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    EditText editSave;
    TextView vievTextLeft;
    TextView vievTextMidle;
    TextView vievTextRigth;

    String text;
    DataBaseHelper dbHelper;

    Button searchBottom;
    RadioGroup radioGroupPos;
    RadioGroup radioGroupLen;
    RadioButton radioButtonMidle;
    RadioButton radioButtonNum5;

    // This function open and copy db to app
    private void copyDataBase(Context myContext) throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Try to open database
        try {
            copyDataBase(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the database from resource directory
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        // Add buttons by id from MainActivity:
        // Big main button - search
        searchBottom = (Button) findViewById(R.id.searchBottom);
        // Groups of check buttons - position of the letters and words length
        radioGroupPos = (RadioGroup) findViewById(R.id.radioGroupPos);
        radioGroupLen = (RadioGroup) findViewById(R.id.radioGroupLen);
        // Let's start app with pushing middle button of letters position
        radioButtonMidle = findViewById(R.id.radioButtonMidle);
        radioButtonMidle.setChecked(true);
        // Let's start app with pushing 5 length of the words
        radioButtonNum5 = findViewById(R.id.radioButton5);
        radioButtonNum5.setChecked(true);

        // This save the Edit text
        editSave = (EditText) findViewById(R.id.et_name);

        // Search views for left, middle and right words views
        vievTextLeft = (TextView) findViewById(R.id.var1);
        vievTextMidle = (TextView) findViewById(R.id.var2);
        vievTextRigth = (TextView) findViewById(R.id.var3);

        // Checks pushing value of check buttons with word length info
        radioGroupLen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            }
        });

        // Checks pushing value of check buttons with letters position info
        radioGroupPos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            }
        });

        // This save the Edit text
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String textQ = prefs.getString("st", text);
        editSave.setText(textQ);

        // Work with main button push event
        searchBottom.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                // Save text from the edit panel
                text = editSave.getText().toString();

                // Change editor value
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("st", text);

                // Save pushing value of check buttons with letters position info
                int selectedIdPos = radioGroupPos.getCheckedRadioButtonId();
                RadioButton radioButtonPos = (RadioButton) radioGroupPos.findViewById(selectedIdPos);
                CharSequence radioButtonTextPos = radioButtonPos.getText();
                // Save pushing value of check buttons with word length info
                int selectedIdLen = radioGroupLen.getCheckedRadioButtonId();
                RadioButton radioButtonLen = (RadioButton) radioGroupLen.findViewById(selectedIdLen);
                CharSequence radioButtonTextLen = radioButtonLen.getText();

                // Here we checks in witch word position shell we search the value from SQL
                if ("First letters".equals(radioButtonTextPos)) {
                    // for the first letters
                    text = text + "%";
                } else if ("Middle letters".equals(radioButtonTextPos)) {
                    // for the nothing matter position
                    text = "%" + text + "%";
                } else {
                    // for the position of the end of the word
                    text = "%" + text;
                }

                // Select query from SQL database with special word condition
                Cursor cursor = db.rawQuery(SQL_QUERY_LIKE, new String[]{text});

                // Create string value for 3 columns
                StringBuilder resultLeft = new StringBuilder();
                StringBuilder resultMidle = new StringBuilder();
                StringBuilder resultRigth = new StringBuilder();

                // Column params
                int allCountWords = ALL_COUNT_WORDS;
                int columnWidth = COLUMN_WIDTH;

                // Set value from SQL query to 3 columns
                int lenWords = Integer.parseInt(radioButtonTextLen.toString());
                if (cursor.moveToFirst()) {
                    do {
                        String column1 = cursor.getString(0);
                        if (column1.length() != lenWords) {
                            continue;
                        }
                        if (columnWidth == COLUMN_WIDTH) {
                            resultLeft.append(column1).append(System.lineSeparator());
                        } else if (columnWidth == COLUMN_WIDTH - 1) {
                            resultMidle.append(column1).append(System.lineSeparator());
                        } else {
                            resultRigth.append(column1).append(System.lineSeparator());
                            columnWidth = COLUMN_WIDTH + 1;
                        }
                        columnWidth--;
                        allCountWords--;
                    } while (cursor.moveToNext() && allCountWords > 0);
                }

                // Set data to 3 column views
                vievTextLeft.setText(resultLeft.toString());
                vievTextMidle.setText(resultMidle.toString());
                vievTextRigth.setText(resultRigth.toString());
                // Save changes
                editor.apply();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Close db
        super.onDestroy();
        dbHelper.close();
    }
}
