package razon.lostandfound.activity;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import razon.lostandfound.R;
import razon.lostandfound.fragment.FoundFragment;
import razon.lostandfound.fragment.InboxFragment;
import razon.lostandfound.fragment.LostFragment;
import razon.lostandfound.fragment.NotificationFragment;
import razon.lostandfound.fragment.ProfileFragment;
import razon.lostandfound.fragment.ProfileListFragment;
import razon.lostandfound.utils.MyTextView;
import razon.lostandfound.utils.SharePreferenceSingleton;
import razon.lostandfound.utils.SimpleActivityTransition;

public class AdminActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    MyTextView adminlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adminlogout = (MyTextView) findViewById(R.id.adminlogout);
        adminlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharePreferenceSingleton.getInstance(AdminActivity.this).saveString("user", "0");
                SharePreferenceSingleton.getInstance(AdminActivity.this).saveString("username", "0");
                SharePreferenceSingleton.getInstance(AdminActivity.this).saveString("propic", "1");

                SimpleActivityTransition.goToPreviousActivity(AdminActivity.this, LoginActivity.class);
                finish();


            }
        });
        worksOnTabViewPager();

    }


    private void worksOnTabViewPager() {

        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);

        TabLayout.Tab homeTab = tabLayout.getTabAt(0);
        homeTab.setIcon(R.drawable.lost_icon);

        TabLayout.Tab foundTab = tabLayout.getTabAt(1);
        foundTab.setIcon(R.drawable.found_icon);

        TabLayout.Tab ratingsTab = tabLayout.getTabAt(2);
        ratingsTab.setIcon(R.drawable.profile_icon);


    }

    private class TabPagerAdapter extends FragmentPagerAdapter {
        public TabPagerAdapter(FragmentManager fragmentManager) {

            super(fragmentManager);

        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    return new LostFragment();
                case 1:
                    return new FoundFragment();
                case 2:
                    return new ProfileListFragment();
                default:
                    return new LostFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Lost";
                case 1:
                    return "Found";
                case 2:
                    return "Profiles";
                default:
                    return "Lost";
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }


}
