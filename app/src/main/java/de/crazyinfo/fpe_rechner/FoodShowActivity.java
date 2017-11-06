package de.crazyinfo.fpe_rechner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FoodShowActivity extends ActionBarActivity {

    FoodDatabase myDB;
    String csvFoodFile = "FPE-CALC_Food.csv";                                            // CSV food file name

    /* Toolbar definieren */
    Toolbar toolbar;
    ActionBar actionBar;
    FoodDatabase FoodDB = new FoodDatabase(this);
    FoodCalcDatabase FoodCalcDB = new FoodCalcDatabase(this);
    ListView listView ;
    ListView listViewCalc;
    SimpleCursorAdapter adapter;
    Button buttonImport;
    Button buttonExport;
    Button buttonAccept;

    final String keyChoSum = "keyChoSum";
    final String keyKcalSum = "keyKcalSum";
    final String keyEditID = "keyEditID";

    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodshow);
        myDB = new FoodDatabase(this);
        listView = (ListView) findViewById(R.id.listViewFood);
        listViewCalc = (ListView) findViewById(R.id.addListFood);
        buttonImport = (Button) findViewById(R.id.buttonImport);
        buttonExport = (Button) findViewById(R.id.buttonExport);
        buttonAccept = (Button) findViewById(R.id.buttonAccept);

        prefs = this.getSharedPreferences("prefsData", MODE_PRIVATE);                              // Datei - prefsData
        prefsEditor = prefs.edit();

        /* Toolbar einrichten */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= 21) {                                  // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                      // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                         // Zurück Button aktiviert
        actionBar.setTitle(getString(R.string.app_name));                              // Anzeige für Titel
        actionBar.setSubtitle(getString(R.string.toolbarFood));                                     // Anzeige für Subtitel

        // Liste aus DB für Lebensmittel
        Cursor resFood = FoodDB.getAllFood();
        if (resFood.getCount() == 0) {
            FoodDB.addFood("Steak \uD83D\uDC37 100g", "0", "112");
            finish();
            startActivity(getIntent());
        }
        else {
            String[] columns = new String[] { "name", "cho", "kcal", "_id" };
            int[] to = new int[] { R.id.name_entry, R.id.cho_entry, R.id.kcal_entry };
            adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, FoodDB.getAllFood(), columns, to, 0);
            listView.setAdapter(adapter);

            // Click für Lebensmittel zur Berechnung hizufügen
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor foodSelect = FoodDB.getFood(id);
                    if (foodSelect.getCount() == 0) {
                        Toast.makeText(getBaseContext(), getString(R.string.toastNoData), Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        while (foodSelect.moveToNext()) {
                            buffer.append("ID: " + foodSelect.getString(0) + "\n");
                            buffer.append("Name: " + foodSelect.getString(1) + "\n");
                            buffer.append("CHO: " + foodSelect.getString(2) + "\n");
                            buffer.append("KCAL: " + foodSelect.getString(3) + "\n");
                            FoodCalcDB.addFoodCalc(foodSelect.getString(1), foodSelect.getString(2), foodSelect.getString(3));
                        }
                        finish();startActivity(getIntent());                                   // Refresh für Ansicht
                    }
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                    prefsEditor.putString(keyEditID, Long.toString(id));
                    prefsEditor.commit();
                    foodManage();
                    // Other Example action items:
                    // Remove Selected Item from ListView
                    return true;
                }
            });
        }

        // Liste der für die Berechnung ausgewählen Lebensmittel
        Cursor resFoodCalc = FoodCalcDB.getAllFoodCalc();
        if (resFoodCalc.getCount() == 0) {
        }
        else {
            String[] columns = new String[]{"name", "cho", "kcal", "_id"};
            int[] to = new int[]{R.id.name_entry, R.id.cho_entry, R.id.kcal_entry};
            adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, FoodCalcDB.getAllFoodCalc(), columns, to, 0);
            listViewCalc.setAdapter(adapter);

            listViewCalc.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                // Click für Lebensmittel von der Berechnungsliste entfernen
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor foodCalcSelect = FoodCalcDB.getFoodCalc(id);
                    if (foodCalcSelect.getCount() == 0) {
                        Toast.makeText(getBaseContext(), getString(R.string.toastNoData), Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuffer buffer = new StringBuffer();

                        while (foodCalcSelect.moveToNext()) {
                            buffer.append("ID: " + foodCalcSelect.getString(0) + "\n");
                            buffer.append("Name: " + foodCalcSelect.getString(1) + "\n");
                            buffer.append("CHO: " + foodCalcSelect.getString(2) + "\n");
                            buffer.append("KCAL: " + foodCalcSelect.getString(3) + "\n");
                            String removeID = foodCalcSelect.getString(0);
                            FoodCalcDB.removeFoodCalc(foodCalcSelect.getString(0));
                        }
                        finish();
                        startActivity(getIntent());                                   // Refresh für Ansicht
                    }
                }
            });
        }

        buttonImport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Permissions Permissions = new Permissions(FoodShowActivity.this);
                boolean permissionResponse = Permissions.checkPermissionForExternalStorage();
                if (!permissionResponse) {
                    Permissions.requestPermissionForExternalStorage();
                }
                importFoodDatabase();
            }

        });
        buttonExport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Permissions Permissions = new Permissions(FoodShowActivity.this);
                boolean permissionResponse = Permissions.checkPermissionForExternalStorage();
                if (!permissionResponse) {
                    Permissions.requestPermissionForExternalStorage();
                }
                boolean exportDone = exportFoodDatabase();
                if (!exportDone) {
                    Toast.makeText(FoodShowActivity.this, getString(R.string.toastExportError), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodShowActivity.this, getString(R.string.toastExportSuccessful), Toast.LENGTH_SHORT).show();
                    finish();startActivity(getIntent());
                }
            }
        });
        buttonAccept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    double choSum = 0;
                    double kcalSum = 0;

                    Cursor foodCalcList = FoodCalcDB.getAllFoodCalc();
                    if (foodCalcList.getCount() == 0) {
                        Toast.makeText(getBaseContext(), getString(R.string.toastNoEatSelected), Toast.LENGTH_SHORT).show();
                    } else {
                       while (foodCalcList.moveToNext()) {
                           choSum = choSum + Double.parseDouble(foodCalcList.getString(2));
                           kcalSum = kcalSum + Double.parseDouble(foodCalcList.getString(3));
                       }
                            String choSumString = Double.toString(choSum).replace(".0", "");
                            String kcalSumString = Double.toString(kcalSum).replace(".0", "");
                            prefsEditor.putString(keyChoSum, choSumString);
                            prefsEditor.putString(keyKcalSum, kcalSumString);
                            prefsEditor.commit();
                            finish();
                        }

                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {                                       // Zurück Button via BackPressed
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void foodManage() {
        Intent intent = new Intent(this, ManageFoodActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();

        Cursor resFood = FoodDB.getAllFood();
        if (resFood.getCount() == 0) {
            FoodDB.addFood("Steak \uD83D\uDC37 100g", "0", "112");
            finish();
            startActivity(getIntent());
        }
        String[] columns = new String[]{"name", "cho", "kcal", "_id"};
        int[] to = new int[]{R.id.name_entry, R.id.cho_entry, R.id.kcal_entry};
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, FoodCalcDB.getAllFoodCalc(), columns, to, 0);
        listViewCalc.setAdapter(adapter);

        String[] columns1 = new String[] { "name", "cho", "kcal", "_id" };
        int[] to1 = new int[] { R.id.name_entry, R.id.cho_entry, R.id.kcal_entry };
        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, FoodDB.getAllFood(), columns1, to1, 0);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super. onBackPressed();
        FoodCalcDB.removeAllFoodCalc(); // FoodCalcDB löschen
    }

    public boolean exportFoodDatabase() {
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                boolean createdir = exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try {
                file = new File(exportDir, csvFoodFile);
                boolean createfile = file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));
                Cursor curCSV = myDB.getAllDataFood();
                printWriter.println(getString(R.string.editTextName) + ";" + getString(R.string.editTextCho) + ";" + getString(R.string.editTextKcal));
                while (curCSV.moveToNext()) {
                    String name = curCSV.getString(0);
                    String cho = curCSV.getString(1);
                    String kcal = curCSV.getString(2);

                    String record = name + ";" + cho + ";" + kcal;
                    printWriter.println(record); // Datensatz in .csv schreiben
                }
            } catch (Exception exc) {
                Toast.makeText(FoodShowActivity.this, getString(R.string.toastExportError), Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (printWriter != null) printWriter.close();
            }
            return true;
        }
    }

    public boolean importFoodDatabase()  {
        try {
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = exportDir.getAbsolutePath() + "/" + csvFoodFile;
            FileReader file = null;

            file = new FileReader(fileName);
            BufferedReader buffer = new BufferedReader(file);

            String line = "";
            line = buffer.readLine();

            while ((line = buffer.readLine()) != null) {
                String[] str = line.split(";", -1);
                String addName = str[0];
                String addCho = str[1];
                String addKcal = str[2];

                File importDone = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + csvFoodFile);

                /* Prüfe Import: addName + addCho + addKcal */
                if (importDone.exists() && line.matches(".*^(?:[^;]*+;){2}[^;]*+$.*") && addName.matches(".*[A-Za-z].*")  && addCho.matches(".*^[0-9]+([.][0-9]+)?$.*") && addKcal.matches(".*^[0-9]+$.*")) {
                } else {
                    Toast.makeText(FoodShowActivity.this, getString(R.string.toastImportError), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                    return false;
                }
                if (addName.length() > 23) {
                    addName = addName.substring(0, 23);
                }
                myDB.addDataFood(addName, addCho, addKcal);
            }
            finish();
            startActivity(getIntent());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(FoodShowActivity.this, getString(R.string.toastImportError), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(FoodShowActivity.this, getString(R.string.toastImportSuccessful), Toast.LENGTH_SHORT).show();
        return true;
    }
}