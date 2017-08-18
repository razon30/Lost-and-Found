package razon.lostandfound.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import razon.lostandfound.R;
import razon.lostandfound.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();


    }
}
