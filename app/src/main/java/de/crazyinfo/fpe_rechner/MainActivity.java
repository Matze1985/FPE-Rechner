package de.crazyinfo.fpe_rechner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    FactorDatabase myDB;
    String csvFactorFile = "FPE-CALC_Factor.csv";                                            // CSV factor file name

    /* Aktuelle Zeit (Stunden erfassen) */
    Date date = new Date();   // given date
    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     */

    /* Variablen für Button, EditText und TextView */

    private Button buttonFactorSettings;                                                            // Button "Factor settings"
    private Button buttonFoodSettings;                                                              // Button "Food settings"
    private Button buttonCalc;                                                                      // Button "Berechnung"
    private EditText editTextCho;                                                                   // Kohlenhydrate
    private EditText editTextKcal;                                                                  // Kcal
    private EditText editTextFactor;                                                                // Faktor
    private TextView textViewResult;                                                                // Ergebnis

    /* AppShortcuts */
    private final static String ACTION_1 = "main";
    private final static String ACTION_2 = "foodshow";
    private final static String ACTION_3 = "converter";
    private final static String ACTION_4 = "info";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    /* SharedPreferences */
    final String keyFactor = "keyFactor";                                                           // Speichert den Wert Faktor
    final String keyChoSum = "keyChoSum";
    final String keyKcalSum = "keyKcalSum";

    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    private GoogleApiClient client;

    FoodCalcDatabase FoodCalcDB = new FoodCalcDatabase(this);

    /* Toolbar definieren */
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new FactorDatabase(this);

        /* AppShortcuts */
        switch (getIntent().getAction()) {
            case ACTION_1:                                                                          // ACTION_1 = Main
                break;
            case ACTION_2:                                                                          // ACTION_2 = FoodShow
                break;
            case ACTION_3:                                                                          // ACTION_3 = Converter
                break;
            case ACTION_4:                                                                          // ACTION_4 = Info
                break;
            default:
                break;
        }

        /* Toolbar einrichten */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {                                                           // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                                               // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);                                                 // Zurück Button deaktiviert

        // Definition App (Button, Text etc. mit Hinweisausgabe)
        buttonFoodSettings = (Button) findViewById(R.id.buttonFoodSettings);
        buttonFactorSettings = (Button) findViewById(R.id.buttonFactorSettings);
        buttonCalc = (Button) findViewById(R.id.buttonCalc);
        editTextCho = (EditText) findViewById(R.id.editTextCho);
        editTextKcal = (EditText) findViewById(R.id.editTextKcal);
        editTextFactor = (EditText) findViewById(R.id.editTextFactor);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        buttonFoodSettings.setOnClickListener(this);
        buttonFactorSettings.setOnClickListener(this);
        buttonCalc.setOnClickListener(this);

        prefs = this.getSharedPreferences("prefsData", MODE_PRIVATE);                              // Datei - prefsData
        prefsEditor = prefs.edit();
        editTextFactor.setText(prefs.getString(keyFactor, ""));
        editTextCho.setText(prefs.getString(keyChoSum, ""));
        editTextKcal.setText(prefs.getString(keyKcalSum, ""));

        // Zeitabhängigen Factor auslesen
        Cursor res1 = myDB.checkFactor(hour);
        if (res1.getCount() == 0) {
            return;
        }
        StringBuffer factorTime = new StringBuffer();
        while (res1.moveToNext()) {
            factorTime.append(res1.getString(1));
        }
        if (factorTime.length() >= 1) editTextFactor.setText(factorTime);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.toolbarConverter) {
            Intent i_Converter = new Intent(this, ConverterActivity.class);                         // Wechsel ConverterActivity
            this.startActivity(i_Converter);
            return true;
        }
        if (id == R.id.toolbarInfo) {
            Intent i_Info = new Intent(this, InfoActivity.class);                                   // Wechsel InfoActivity
            this.startActivity(i_Info);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // OnClick wird aufgerufen, wenn geklickt wird

    @Override
    public void onClick(View v) {

        /* Wenn Button gedrückt wird, dann schließt Tastatur automatisch */
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (v == buttonFactorSettings) {
            Cursor res = myDB.getAllDataFactor();
            if (res.getCount() == 0) {
                showMessage(getString(R.string.showMsgFactorTitle),getString(R.string.toastNoData));
            } else {
                StringBuffer buffer = new StringBuffer();

                while (res.moveToNext()) {
                    if (res.getString(0).length() <= 1) buffer.append("");
                    buffer.append(res.getString(0).replaceAll("\\b(\\d)\\b", "0$1") + " " + getString(R.string.showMsgClock) + "     ");
                    buffer.append(getString(R.string.editTextFactor) + ": " + res.getString(1) + "\n");
                }
                showMessage(getString(R.string.showMsgFactorTitle), buffer.toString());
            }
        }

        /* Lebensmittel hinzufügen */
        if (v == buttonFoodSettings) {
            foodPart();
        }

        if (v == buttonCalc) {

            /* Variablen deklarieren */
            double cho;                 // Kohlenhydrate
            double kcal;                // Kcal
            double factor;              // Faktor
            double insulin;             // Insulin
            double fpu;                 // Fett-Protein-Einheiten gerundet

            /* Diese erhält den Wert des Feldes als Zahl (Kohlenhydrate & Kcal & Faktor)
             * Nummernformat überprüfen für die Ergebnisausgabe via try and catch */
            try {
                cho = Float.parseFloat(editTextCho.getText().toString());
                kcal = Float.parseFloat(editTextKcal.getText().toString());
                factor = Float.parseFloat(editTextFactor.getText().toString());
            } catch (NumberFormatException nfe) {
                textViewResult.setText("");
                Toast.makeText(this, getString(R.string.toastNoValue), Toast.LENGTH_SHORT).show();
                return;
            }

            /* Factor mit Zeitvermerk in Datenbank schreiben */
            boolean isInserted = myDB.addDataFactor(hour, editTextFactor.getText().toString());

            /* Berechnungen */
            cho = Math.abs(cho * 4);                                                                // Kohlenhydrate * 4
            fpu = (double) Math.round(((kcal - cho) / 100 * 10) * 1) / 10;                          // FPE-Gehalt auf eine Nachkommastelle gerundet
            insulin = (double) Math.round(((kcal - cho) / 100 * factor * 10) * 1) / 10;             // Insulinmenge auf eine Nachkommastelle gerundet

            /*  Stundenvergleich für die Insulinabgabe */
            String[] arrayCheckHours = {"3", "4", "5", "7 - 8", "0"};
            String checkHours = "0";

            if (kcal >= 100 && kcal < 200) {
                System.out.println(arrayCheckHours[0]);         // 3 Stunden
                checkHours = arrayCheckHours[0].toString();
            }
            if (kcal >= 200 && kcal < 300) {
                System.out.println(arrayCheckHours[1]);         // 4 Stunden
                checkHours = arrayCheckHours[1].toString();
            }
            if (kcal >= 300 && kcal < 400) {
                System.out.println(arrayCheckHours[2]);         // 5 Stunden
                checkHours = arrayCheckHours[2].toString();
            }
            if (kcal >= 400) {
                System.out.println(arrayCheckHours[3]);         // 7 - 8 Stunden
                checkHours = arrayCheckHours[3].toString();
            }
            if (insulin <= 0) {
                System.out.println(arrayCheckHours[4]);         // Wenn Insulin kleiner gleich 0, dann auch 0 Stunden
                checkHours = arrayCheckHours[4].toString();
            }
            if (fpu <= 0) {
                System.out.println(arrayCheckHours[4]);
                checkHours = arrayCheckHours[4].toString();     // Wenn Fett-Protein-Einheiten kleiner gleich 0, dann auch 0 Stunden
            }

            /*  Insulinabgabe prüfen*/
            double[] arrayInsulin = {0};
            if (insulin < 0) {
                insulin = arrayInsulin[0];                      // Wenn Insulin kleiner 0, dann auch Insulin 0
            }
            if (kcal < 100) {
                insulin = arrayInsulin[0];                      // Wenn Kcal kleiner 0, dann auch Insulin 0
            }

            /*  FPE-Gehalt prüfen*/
            double[] arrayFpu = {0};
            if (fpu < 0) {
                fpu = arrayFpu[0];                              // Wenn FPE kleiner 0, dann auch FPE 0
            }
            if (insulin <= 0) {
                fpu = arrayFpu[0];                              // Wenn Insulin kleiner gleich 0, dann auch FPE 0
            }

            /* Ergebnisausgabe */
            textViewResult.setText(getString(R.string.calcResult) + fpu + getString(R.string.textFpu) + insulin + getString(R.string.textAmountOfInsulin) + checkHours + getString(R.string.textHours));

            /* Preferences: Wert in das Feld EditText setzen */
            prefsEditor.putString(keyChoSum, editTextCho.getText().toString());
            prefsEditor.putString(keyKcalSum, editTextKcal.getText().toString());
            prefsEditor.putString(keyFactor, editTextFactor.getText().toString());
            prefsEditor.commit();
            FoodCalcDB.removeAllFoodCalc();
        }
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.setNeutralButton(getString(R.string.buttonCancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(getString(R.string.buttonImport),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Permissions Permissions = new Permissions(MainActivity.this);
                        boolean permissionResponse = Permissions.checkPermissionForExternalStorage();
                        if (!permissionResponse) {
                            Permissions.requestPermissionForExternalStorage();
                        }
                        importFactorDatabase();
                    }
                });

        builder.setPositiveButton(getString(R.string.buttonExport),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Permissions Permissions = new Permissions(MainActivity.this);
                        boolean permissionResponse = Permissions.checkPermissionForExternalStorage();
                        if (!permissionResponse) {
                            Permissions.requestPermissionForExternalStorage();
                        }
                        boolean exportDone = exportFactorDatabase();
                        if (!exportDone) {
                            Toast.makeText(MainActivity.this, getString(R.string.toastExportError), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.toastExportSuccessful) + " " + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_SHORT).show();
                            finish();startActivity(getIntent());
                        }
                    }
                });
        builder.show();
    }

    private void foodPart() {
        Intent intent = new Intent(this, FoodShowActivity.class);
        startActivity(intent);
    }

    /* Berechnungsübergabe von FoodShowActivity */
    @Override
    public void onResume() {
        super.onResume();
        editTextCho.setText(prefs.getString(keyChoSum, ""));
        editTextKcal.setText(prefs.getString(keyKcalSum, ""));
        textViewResult.setText("");
    }

    @Override
    public void onBackPressed() {
        super. onBackPressed();
        prefsEditor.remove(keyChoSum);
        prefsEditor.remove(keyKcalSum);
        prefsEditor.commit();
    }

    public boolean exportFactorDatabase() {
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
                file = new File(exportDir, csvFactorFile);
                boolean createfile = file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                Cursor curCSV = myDB.getAllDataFactor();

                printWriter.println(getString(R.string.textTime) + ";" + getString(R.string.editTextFactor));
                while (curCSV.moveToNext()) {
                    String hour = curCSV.getString(0);
                    String factor = curCSV.getString(1);

                    String record = hour + ";" + factor;
                    printWriter.println(record); // Datensatz in .csv schreiben
                }
            } catch (Exception exc) {
                Toast.makeText(MainActivity.this, getString(R.string.toastExportError), Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (printWriter != null) printWriter.close();
            }
            return true;
        }
    }

    public boolean importFactorDatabase()  {
        try {
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = exportDir.getAbsolutePath() + "/" + csvFactorFile;
            FileReader file = null;

            file = new FileReader(fileName);
            BufferedReader buffer = new BufferedReader(file);

            String line = "";
            line = buffer.readLine();

            while ((line = buffer.readLine()) != null) {
                String[] str = line.split(";", -1);
                String addHour = str[0];
                String addFactor = str[1];

                File importDone = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + csvFactorFile);

                /* Prüfe Import: addHour + addFactor */
                if (importDone.exists() && line.matches(".*^(?:[^;]*+;){1}[^;]*+$.*") && addHour.matches(".*^([0-9]|1[0-9]|2[0-3])$.*") && addFactor.matches(".*^[0-9]+([.][0-9]+)?$.*")) {
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.toastImportError), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                    return false;
                }
                myDB.addDataFactor(Integer.parseInt(addHour), addFactor);
            }
            finish();
            startActivity(getIntent());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.toastImportError), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(MainActivity.this, getString(R.string.toastImportSuccessful), Toast.LENGTH_SHORT).show();
        return true;
    }
}