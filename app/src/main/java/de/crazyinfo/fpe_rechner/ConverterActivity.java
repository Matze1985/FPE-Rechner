package de.crazyinfo.fpe_rechner;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class ConverterActivity extends ActionBarActivity implements View.OnClickListener {

    /* Toolbar definieren */
    Toolbar toolbar;
    ActionBar actionBar;

    /* Variablen für Button, EditText und TextView */
    public Button buttonMgdl;                                                                               // Button "Berechnung mmol/l"
    public Button buttonMmoll;                                                                              // Button "Berechnung mg/dl"
    public TextView textViewResult;                                                                         // Ergebnis
    public EditText editTextConverterInput;                                                                 // Wert eingeben

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        /* Toolbar einrichten */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= 21) {                                                                 // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                                                     // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                                                         // Zurück Button aktiviert
        actionBar.setTitle(getString(R.string.app_name));                                                              // Anzeige für Titel
        actionBar.setSubtitle(getString(R.string.toolbarConverter));                                                           // Anzeige für Subtitel

        // Definition App (Button, Text etc. mit Hinweisausgabe)
        editTextConverterInput = (EditText) findViewById(R.id.editTextConverterInput);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        buttonMmoll = (Button) findViewById(R.id.buttonMmoll);
        buttonMgdl = (Button) findViewById(R.id.buttonMgdl);

        // Calling onClick() method
        buttonMmoll.setOnClickListener(this);
        buttonMgdl.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {                                                              // Zurück Button via BackPressed
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        /* Wenn Button gedrückt wird, dann schließt Tastatur automatisch */
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        /* Variablen deklarieren */
        double converter;
        double mgdlSubResult;
        double mmollSubResult;

        /* Diese erhält den Wert des Feldes als Zahl (Wert)
         * Nummernformat überprüfen für die Ergebnisausgabe via try and catch */
        try {
            converter = Float.parseFloat(editTextConverterInput.getText().toString());
        } catch (NumberFormatException nfe) {
            textViewResult.setText("");
            Toast.makeText(this, getString(R.string.toastNoValue), Toast.LENGTH_SHORT).show();
            return;
        }

        /* Berechnungen */
        Locale.setDefault(new Locale("en", "US"));                                     // Punkt statt Komma verwenden
        mgdlSubResult = Math.abs(converter * 0.0555);                                                   // Teilergebnis mg/dl berechnen
        String mgdlResult = String.format("%.1f", mgdlSubResult);                                       // Ergebnis ohne Komma gerundet
        mmollSubResult = Math.abs(converter * 18.0182);                                                 // Teilergebnis mmol/l berechnen
        String mmollResult = String.format(Locale.ROOT, "%.0f", mmollSubResult);                 // Ergebnis mit Komma gerundet

        switch (v.getId()) {
            case R.id.buttonMmoll:
                textViewResult.setText(getString(R.string.calcResult) + mgdlResult + " mmol/l");
                break;
            case R.id.buttonMgdl:
                textViewResult.setText(getString(R.string.calcResult) + mmollResult + " mg/dl");
                break;
            default:
                break;
        }
    }
}