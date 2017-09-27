package razon.lostandfound.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import razon.lostandfound.R;
import razon.lostandfound.fragment.FoundFragment;
import razon.lostandfound.fragment.InboxFragment;
import razon.lostandfound.fragment.LostFragment;
import razon.lostandfound.fragment.NotificationFragment;
import razon.lostandfound.fragment.ProfileFragment;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.utils.Fab;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.SharePreferenceSingleton;

public class HomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    public static UserGeneralInfo userGeneralInfo;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialization();
        worksOnTabViewPager();
        worksOnFab();

    }

    private void worksOnFab() {

        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = Color.parseColor("#FFFFFF");
        int fabColor = getResources().getColor(R.color.colorPrimaryDark);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });


      findViewById(R.id.addlost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();

                goToNextActivity("lost");

            }
        });
      findViewById(R.id.addfound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();

                goToNextActivity("found");

            }
        });



    }

    public void goToNextActivity(String status) {
        Intent intent = new Intent(this, MainActivity.class).putExtra("type", FragmentNode.ADD_ITEM)
                .putExtra("status",status);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }


    private void initialization() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        String username = SharePreferenceSingleton.getInstance(this).getString("username");

        reference = FirebaseDatabase.getInstance().getReference().child("UserData").child(username);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userGeneralInfo = dataSnapshot.child("generalInfo").getValue(UserGeneralInfo.class);
                if (userGeneralInfo != null) {
                    SharePreferenceSingleton.getInstance(HomeActivity.this).saveString("name",userGeneralInfo.getName());
                }

                if (dataSnapshot.hasChild(FirebaseEndPoint.IMAGE)) {
                    String image = dataSnapshot.child(FirebaseEndPoint.IMAGE).getValue().toString();
                    byte[] data = Base64.decode(image, Base64.DEFAULT);

                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    profileImage.setImageBitmap(bmp);

                    SharePreferenceSingleton.getInstance(HomeActivity.this).saveString("propic",image);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

       reference.addValueEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {

        if (valueEventListener!=null && reference!=null){
            reference.removeEventListener(valueEventListener);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (valueEventListener!=null && reference!=null){
            reference.removeEventListener(valueEventListener);
        }
        super.onStop();
    }

    private void worksOnTabViewPager() {

        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab homeTab = tabLayout.getTabAt(0);
        homeTab.setIcon(R.drawable.lost_icon);
        TabLayout.Tab foundTab = tabLayout.getTabAt(1);
        foundTab.setIcon(R.drawable.found_icon);
        TabLayout.Tab earningTab = tabLayout.getTabAt(2);
        if (bmp==null) {
            earningTab.setIcon(R.drawable.profile_icon);
        }else {
            Drawable d = new BitmapDrawable(getResources(), bmp);
            earningTab.setIcon(d);
        }
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
