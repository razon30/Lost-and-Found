package razon.lostandfound.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import razon.lostandfound.R;
import razon.lostandfound.fragment.AddItemFragment;
import razon.lostandfound.fragment.ChatDetailsFragment;
import razon.lostandfound.fragment.FoundDetailsFragment;
import razon.lostandfound.fragment.LostDetailsFragment;
import razon.lostandfound.utils.FragmentNode;

public class MainActivity extends AppCompatActivity {

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        }

    }
}
