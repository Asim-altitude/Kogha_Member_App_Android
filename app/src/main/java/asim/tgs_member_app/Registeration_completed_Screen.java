package asim.tgs_member_app;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import asim.tgs_member_app.models.Constants;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Map;


public class Registeration_completed_Screen extends AppCompatActivity {

    LinearLayout done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration_completed__screen);

        done = findViewById(R.id.done_btn);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registeration_completed_Screen.this,SetupPassword.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();

            }
        });



    }

    @Override
    public void onBackPressed() {

    }
}
