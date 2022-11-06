package xyz.slkagura.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import xyz.slkagura.ui.view.main.MainFragment;

public class AppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initFragment();
    }
    
    private void initFragment() {
        Fragment fragment = MainFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, fragment).addToBackStack("Decode").commit();
    }
}
