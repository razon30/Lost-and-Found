package razon.lostandfound.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import razon.lostandfound.R;
import razon.lostandfound.fragment.AddItemFragment;
import razon.lostandfound.fragment.ChatDetailsFragment;
import razon.lostandfound.fragment.FoundDetailsFragment;
import razon.lostandfound.fragment.LostDetailsFragment;
import razon.lostandfound.fragment.ProfileFragment;
import razon.lostandfound.fragment.UpdateProfileFragment;
import razon.lostandfound.utils.FragmentNode;

public class MainActivity extends AppCompatActivity {

    String type;
    public static ArrayList<String> userNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameList = new ArrayList<String>();

        type = getIntent().getStringExtra("type");

        switch (type){

            case FragmentNode.LOST:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LostDetailsFragment()).commit();
                break;
            case FragmentNode.FOUND:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FoundDetailsFragment()).commit();
                break;
            case FragmentNode.CHAT:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatDetailsFragment()).commit();
                break;
            case FragmentNode.ADD_ITEM:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemFragment()).commit();
                break;
            case FragmentNode.PROFILE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case FragmentNode.EDIT_PROFILE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpdateProfileFragment()).commit();
                break;

        }

    }
}
