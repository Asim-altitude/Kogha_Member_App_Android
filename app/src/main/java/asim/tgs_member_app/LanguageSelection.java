package asim.tgs_member_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import asim.tgs_member_app.models.Constants;
import asim.tgs_member_app.utils.LanguageConfig;


public class LanguageSelection extends AppCompatActivity {

    RadioButton bahasa,english;
    RelativeLayout english_lay,bahasa_lay;
    Button change_lang_btn;

    String selected_language = "English";

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        setupToolbar();
        LoadLocalConfigurations();
        /*setTitle("Language");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/

        english_lay = (RelativeLayout) findViewById(R.id.english_lang_lay);
        bahasa_lay = (RelativeLayout) findViewById(R.id.bahasa_lang_lay);
        change_lang_btn = (Button) findViewById(R.id.change_lang_btn);

        ((RadioButton)findViewById(R.id.english_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    ((RadioButton)findViewById(R.id.bahasa_btn)).setChecked(false);
                }
            }
        });

        ((RadioButton)findViewById(R.id.bahasa_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    ((RadioButton)findViewById(R.id.english_btn)).setChecked(false);
                }
            }
        });

        english_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RadioButton)findViewById(R.id.english_btn)).setChecked(true);
                selected_language = "English";
            }
        });
        bahasa_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RadioButton)findViewById(R.id.bahasa_btn)).setChecked(true);
                selected_language = "Bahasa Malayu";
            }
        });

        change_lang_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton)findViewById(R.id.bahasa_btn)).isChecked())
                    Changelanguage("ms");
                else
                    Changelanguage("en");
            }
        });

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Language");
    }
    private void LoadLocalConfigurations()
    {
        String language = settings.getString(Constants.PREF_LOCAL,"");
        if (!language.equalsIgnoreCase(""))
        {
            if (language.equalsIgnoreCase("English"))
            {
                ((RadioButton)findViewById(R.id.english_btn)).setChecked(true);
                ((RadioButton)findViewById(R.id.bahasa_btn)).setChecked(false);
                selected_language = "English";
            }
            else
            {
                ((RadioButton)findViewById(R.id.english_btn)).setChecked(false);
                ((RadioButton)findViewById(R.id.bahasa_btn)).setChecked(true);
                selected_language = "Bahasa Malayu";
            }

        }
    }
    private Dialog dialog;
    private void Changelanguage(final String locale)
    {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                .setMessage("Your language will be changed inside application.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LanguageConfig.setLocale(getApplicationContext(),locale);
                    }
                });

        dialog = builder.create();
        dialog.show();*/

        LanguageConfig.setLocale(getApplicationContext(),locale);
        settings.edit().putString(Constants.PREF_LOCAL,selected_language).apply();
        Toast.makeText(getApplicationContext(),"Your language changed to "+selected_language,Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(getApplicationContext(),DrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(),DrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
