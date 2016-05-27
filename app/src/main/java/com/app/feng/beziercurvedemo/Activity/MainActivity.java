package com.app.feng.beziercurvedemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.app.feng.beziercurvedemo.R;
import com.app.feng.beziercurvedemo.View.BezierPainter;

public class MainActivity extends AppCompatActivity {
    private BezierPainter bezierPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        bezierPainter = (BezierPainter) findViewById(R.id.view);
    }

    public void switchPoint(View view) {
        switch (view.getId()) {
            case R.id.con1:
                bezierPainter.setWhichControl(BezierPainter.CON1_POINT);
                break;
            case R.id.con2:
                bezierPainter.setWhichControl(BezierPainter.CON2_POINT);
                break;
            case R.id.start:
                bezierPainter.setWhichControl(BezierPainter.START_POINT);
                break;
            case R.id.end:
                bezierPainter.setWhichControl(BezierPainter.END_POINT);
                break;
        }
    }

    public void start(View view){
        Intent intent = new Intent(getBaseContext(), NewActivity.class);
        startActivity(intent);
    }
}
