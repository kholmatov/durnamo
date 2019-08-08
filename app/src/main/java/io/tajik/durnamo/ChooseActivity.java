package io.tajik.durnamo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import io.agora.durnamo.R;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        LinearLayout vol = findViewById(R.id.vol);
        vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //открываем окно регистрицию для волантера
                startActivity(new Intent(getApplicationContext(), VideoChatVolViewActivity.class));
            }
        });

        LinearLayout loz = findViewById(R.id.loz);
        loz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //открываем окно регистрицию для ЛОВЗ
                startActivity(new Intent(getApplicationContext(), LovzActivity.class));
            }
        });
    }
}
