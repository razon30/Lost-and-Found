package razon.lostandfound.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import razon.lostandfound.R;
import razon.lostandfound.fragment.FoundFragment;
import razon.lostandfound.fragment.InboxFragment;
import razon.lostandfound.fragment.LostFragment;
import razon.lostandfound.fragment.NotificationFragment;
import razon.lostandfound.fragment.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialization();
        worksOnTabViewPager();

    }

    private void initialization() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void worksOnTabViewPager() {

        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab homeTab = tabLayout.getTabAt(0);
        homeTab.setIcon(R.drawable.lost_icon);
        TabLayout.Tab foundTab = tabLayout.getTabAt(1);
        foundTab.setIcon(R.drawable.found_icon);
        TabLayout.Tab earningTab = tabLayout.getTabAt(2);
        earningTab.setIcon(R.drawable.profile_icon);
        TabLayout.Tab ratingsTab = tabLayout.getTabAt(3);
        ratingsTab.setIcon(R.drawable.notification_icon);
        TabLayout.Tab accountTab = tabLayout.getTabAt(4);
        accountTab.setIcon(R.drawable.inbox_ixon);

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
                    return new ProfileFragment();
                case 3:
                    return new NotificationFragment();
                case 4:
                    return new InboxFragment();
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
                    return "Profile";
                case 3:
                    return "Noti";
                case 4:
                    return "Inbox";
                default:
                    return "Lost";
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

    }


}
