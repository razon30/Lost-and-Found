package razon.lostandfound.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import razon.lostandfound.R;
import razon.lostandfound.fragment.LoginFragment;
import razon.lostandfound.utils.SharePreferenceSingleton;
import razon.lostandfound.utils.SimpleActivityTransition;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharePreferenceSingleton.getInstance(this).getString("user").equals("1")){
            SimpleActivityTransition.goToNextActivity(this, HomeActivity.class);
            finish();
        }else {

            setContentView(R.layout.activity_login);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        }

    }
}
