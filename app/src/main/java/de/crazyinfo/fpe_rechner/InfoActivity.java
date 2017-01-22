package de.crazyinfo.fpe_rechner;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class InfoActivity extends ActionBarActivity {

    /* Toolbar definieren */
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        /* Toolbar einrichten */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= 21) {                                   // Abfrage für ältere Versionen verhindert App-Absturz
            toolbar.setElevation(10);                                       // Schattenstärke der Toolbar
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                          // Zurück Button aktiviert
        actionBar.setTitle(getString(R.string.app_name));                                // Anzeige für Titel
        actionBar.setSubtitle(getString(R.string.toolbarInfo));                                       // Anzeige für Subtitel
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
}

