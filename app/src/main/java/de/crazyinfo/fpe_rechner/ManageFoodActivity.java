package de.crazyinfo.fpe_rechner;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by l.boettcher on 17.10.2017.
 */

public class ManageFoodActivity extends FoodShowActivity {

    Toolbar toolbar;
    ActionBar actionBar;
    FoodDatabase FoodDB = new FoodDatabase(this);

    final String keyEditID = "keyEditID";

    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    private EditText editCho;                                                                   // Kohlenhydrate
    private EditText editKcal;                                                                  // Kcal
    private EditText editName;                                                                  // Name
    Button buttonNew;
    Button buttonChange;
    Button buttonDelete;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managefood);
        myDB = new FoodDatabase(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= 21) {                                  // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                      // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                         // Zurück Button aktiviert
        actionBar.setTitle(getString(R.string.app_name));                              // Anzeige für Titel
        actionBar.setSubtitle(getString(R.string.toolbarFood));                                     // Anzeige für Subtitel

        editCho = (EditText) findViewById(R.id.editCho);
        editKcal = (EditText) findViewById(R.id.editKcal);
        editName = (EditText) findViewById(R.id.editName);
        buttonNew = (Button) findViewById(R.id.buttonNew);
        buttonChange = (Button) findViewById(R.id.buttonChange);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        prefs = this.getSharedPreferences("prefsData", MODE_PRIVATE);                              // Datei - prefsData
        prefsEditor = prefs.edit();
        String strid = prefs.getString(keyEditID, "");
        final long id = Long.parseLong(strid);

        final Cursor foodSelect = FoodDB.getFood(id);
        if (foodSelect.getCount() == 0) {
            Toast.makeText(getBaseContext(), getString(R.string.toastNoData), Toast.LENGTH_SHORT).show();
        } else {
            StringBuffer buffer = new StringBuffer();

            while (foodSelect.moveToNext()) {
                buffer.append("ID: " + foodSelect.getString(0) + "\n");
                buffer.append("Name: " + foodSelect.getString(1) + "\n");
                buffer.append("CHO: " + foodSelect.getString(2) + "\n");
                buffer.append("KCAL: " + foodSelect.getString(3) + "\n");

                editName.setText(foodSelect.getString(1));
                editCho.setText(foodSelect.getString(2));
                editKcal.setText(foodSelect.getString(3));
            }
            buttonNew.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String addCho = editCho.getText().toString();
                    String addKcal = editKcal.getText().toString();
                    String addName = editName.getText().toString();

                    /* Leereingabe mit "0" ersetzen und "Dummy" und "Textlänge" für Name */
                    if (addCho.equals("")) {
                        addCho = "0";
                    }
                    if (addKcal.equals("")) {
                        addKcal = "0";
                    }
                    if (addName.equals("")) {
                        addName = "Dummy (Empty)";
                    }
                    if (addName.matches(".*[A-Za-z].*")) {
                    } else {
                        addName = "Dummy (A-Z)";
                    }
                    if (addName.length() > 23) {
                        addName = addName.substring(0, 23);
                    }

                    Cursor foodSelect = FoodDB.checkFood(addName);
                    if (foodSelect.getCount() == 0) {
                        FoodDB.addFood(addName, addCho, addKcal);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.toastDoubleEntry), Toast.LENGTH_SHORT).show();
                    }
                }

            });
            buttonChange.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String addCho = editCho.getText().toString();
                    String addKcal = editKcal.getText().toString();
                    String addName = editName.getText().toString();

                    /* Leereingabe mit "0" ersetzen und "Dummy", "," für Name */
                    if (addCho.equals("")) {
                        addCho = "0";
                    }
                    if (addKcal.equals("")) {
                        addKcal = "0";
                    }
                    if (addName.equals("")) {
                        addName = "Dummy (Empty)";
                    }
                    if (addName.matches(".*[A-Za-z].*")) {
                    } else {
                        addName = "Dummy (A-Z)";
                    }
                    if (addName.matches(".*[;].*")) {
                        addName = addName.replace(";", ",");
                    }
                    if (addName.length() > 23) {
                        addName = addName.substring(0, 23);
                    }

                    FoodDB.updateFood(id, addName, addCho, addKcal);
                    finish();
                }

            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FoodDB.removeFood(id);
                    finish();
                }

            });
            }
        }
}