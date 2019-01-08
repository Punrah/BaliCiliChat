package com.djinggamedia.datapengungsi.appcs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djinggamedia.datapengungsi.appcs.adapter.CustomViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();



    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    LinearLayout button1;
    LinearLayout button2;
    LinearLayout button3;
    LinearLayout button4;

    ImageView icon1;
    ImageView icon2;
    ImageView icon3;
    ImageView icon4;

    TextView text1,text2,text3,text4;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(true);
        setupViewPager(viewPager);
viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
tabAdapter(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
});

        button1 = (LinearLayout) findViewById(R.id.button1);
        button2 = (LinearLayout) findViewById(R.id.button2);
        button3 = (LinearLayout) findViewById(R.id.button3);
        button4 = (LinearLayout) findViewById(R.id.button4);

        icon1= (ImageView) findViewById(R.id.icon1);
        icon2= (ImageView) findViewById(R.id.icon2);
        icon3= (ImageView) findViewById(R.id.icon3);
        icon4= (ImageView) findViewById(R.id.icon4);

        text1= (TextView) findViewById(R.id.text1);
        text2= (TextView) findViewById(R.id.text2);
        text3= (TextView) findViewById(R.id.text3);
        text4= (TextView) findViewById(R.id.text4);

        icon1.setImageResource(R.drawable.contact);
        icon2.setImageResource(R.drawable.chat);
        icon3.setImageResource(R.drawable.event);
        icon4.setImageResource(R.drawable.settings);

        tabAdapter(0);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(1);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(2);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(3);
            }
        });




    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
                        // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
    }
    // [END on_start_check_user]




    private void tabAdapter(int no)
    {
        viewPager.setCurrentItem(no);
        if(no==0)
        {

            icon1.setImageResource(R.drawable.contact_active);
            icon2.setImageResource(R.drawable.chat);
            icon3.setImageResource(R.drawable.event);
            icon4.setImageResource(R.drawable.settings);
            text1.setTextColor(Color.parseColor("#ff9c3a"));
            text2.setTextColor(Color.parseColor("#cccccc"));
            text3.setTextColor(Color.parseColor("#cccccc"));
            text4.setTextColor(Color.parseColor("#cccccc"));

        }
        else  if(no==1)
        {
            icon1.setImageResource(R.drawable.contact);
            icon2.setImageResource(R.drawable.chat_active);
            icon3.setImageResource(R.drawable.event);
            icon4.setImageResource(R.drawable.settings);
            text1.setTextColor(Color.parseColor("#cccccc"));
            text2.setTextColor(Color.parseColor("#ff9c3a"));
            text3.setTextColor(Color.parseColor("#cccccc"));
            text4.setTextColor(Color.parseColor("#cccccc"));
        }
        else if(no==2)
        {
            icon1.setImageResource(R.drawable.contact);
            icon2.setImageResource(R.drawable.chat);
            icon3.setImageResource(R.drawable.event_active);
            icon4.setImageResource(R.drawable.settings);
            text1.setTextColor(Color.parseColor("#cccccc"));
            text2.setTextColor(Color.parseColor("#cccccc"));
            text3.setTextColor(Color.parseColor("#ff9c3a"));
            text4.setTextColor(Color.parseColor("#cccccc"));
        }
        else  if(no==3)
        {
            icon1.setImageResource(R.drawable.contact);
            icon2.setImageResource(R.drawable.chat);
            icon3.setImageResource(R.drawable.event);
            icon4.setImageResource(R.drawable.settings_active);
            text1.setTextColor(Color.parseColor("#cccccc"));
            text2.setTextColor(Color.parseColor("#cccccc"));
            text3.setTextColor(Color.parseColor("#cccccc"));
            text4.setTextColor(Color.parseColor("#ff9c3a"));
        }

    }




    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ContactFragment(), "ONE");
        adapter.addFrag(new ChatFragment(), "TWO");
        adapter.addFrag(new SettingsFragment(), "THREE");
        adapter.addFrag(new ProfileFragment(), "FOUR");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }


    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

}
