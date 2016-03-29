package com.cc.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.library.PatternView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PatternView mPatternView;
    private Button mSetupBtn, mAuthenticBtn;
    private TextView mTips;

    private boolean mIsPasswordSet;
    private String mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPatternView = (PatternView) findViewById(R.id.patternView);
        mTips = (TextView) findViewById(R.id.pattern_tips);
        mSetupBtn = (Button) findViewById(R.id.mode_setup_btn);
        mSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               initSetupMode();
            }
        });

        mAuthenticBtn = (Button) findViewById(R.id.authentic_btn);
        mAuthenticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAuthenticMode();
            }
        });
        initSetupMode();
    }

    private void initSetupMode() {
        mPatternView.setIsSetup(true);
        mIsPasswordSet = false;
        mPassword = "";
        mTips.setText("Draw an unlock pattern");
        mPatternView.setOnFinishListener(new PatternView.OnFinishListener() {
            @Override
            public boolean onFinish(PatternView patternView, List<Integer> result, String resultAsString) {

                if (mIsPasswordSet) {
                    String password = resultAsString;
                    if (password.equals(mPassword)) {
                        Toast.makeText(MainActivity.this, "Password has setup successfully!", Toast.LENGTH_LONG).show();
                        mPatternView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initAuthenticMode();
                            }
                        }, 1000);
                        return true;
                    }
                    mTips.setText("Try again");
                    return false;
                } else {
                    if (result.size() < 4) {
                        mTips.setText("Connect at least 4 dots, Try again.");
                        return false;
                    }
                    mIsPasswordSet = true;
                    mPassword = resultAsString;
                    mTips.setText("Draw pattern again to confirm");
                    return true;
                }

            }
        });
    }

    private void initAuthenticMode() {
        mPatternView.setIsSetup(false);
        mIsPasswordSet = false;
        mTips.setText("Draw pattern to unlock");

        mPatternView.setOnFinishListener(new PatternView.OnFinishListener() {
            @Override
            public boolean onFinish(PatternView patternView, List<Integer> result, String resultAsString) {
                if (resultAsString.equals(mPassword)) {
                    Toast.makeText(MainActivity.this, "Unlock successful", Toast.LENGTH_LONG).show();
                    mPatternView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTips.setText("Draw pattern to unlock");
                            ;
                        }
                    }, 1000);

                    return true;
                }
                mTips.setText("Try again");
                return false;
            }
        });
    }
}
