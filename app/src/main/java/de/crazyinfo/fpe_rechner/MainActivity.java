package de.crazyinfo.fpe_rechner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

public class MainActivity extends ActionBarActivity

        implements OnClickListener {

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     */

    /* Variablen für Button, EditText und TextView */
    private Button buttonCalc;                                                                      // Button "Berechnung"
    private EditText editTextCho;                                                                   // Kohlenhydrate
    private EditText editTextKcal;                                                                  // Kcal
    private EditText editTextFactor;                                                                // Faktor
    private TextView textViewResult;                                                                // Ergebnis

    /* AppShortcuts */
    private final static String ACTION_1 = "main";
    private final static String ACTION_2 = "info";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    /* SharedPreferences */
    final String keyFactor = "keyFactor";                                                           // Speichert den Wert Faktor

    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    private GoogleApiClient client;

    /* Toolbar definieren */
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* AppShortcuts */
        switch (getIntent().getAction()){
            case ACTION_1:                                                                          // ACTION_1 = Main
                break;
            case ACTION_2:                                                                          // ACTION_2 = Info
                break;
            default:
                break;
        }

        /* Toolbar einrichten */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= 21) {                                                           // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                                               // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);                                                 // Zurück Button deaktiviert

        // Definition App (Button, Text etc. mit Hinweisausgabe)
        buttonCalc = (Button) findViewById(R.id.buttonCalc);
        editTextCho = (EditText) findViewById(R.id.editTextCho);
        editTextKcal = (EditText) findViewById(R.id.editTextKcal);
        editTextFactor = (EditText) findViewById(R.id.editTextFactor);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        buttonCalc.setOnClickListener(this);

        prefs = this.getSharedPreferences("prefsData", MODE_PRIVATE);                              // Datei - prefsData
        prefsEditor = prefs.edit();
        editTextFactor.setText(prefs.getString(keyFactor, ""));

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
        if (id == R.id.toolbarInfo) {
            Intent i_Info = new Intent(this, InfoActivity.class);
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
            }
            catch (NumberFormatException nfe)
                {
                    textViewResult.setText("");
                    Toast.makeText(this, getString(R.string.toastNoValue), Toast.LENGTH_SHORT).show();
                    return;
                }

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
            prefsEditor.putString(keyFactor, editTextFactor.getText().toString());
            prefsEditor.commit();
        }
    }
}