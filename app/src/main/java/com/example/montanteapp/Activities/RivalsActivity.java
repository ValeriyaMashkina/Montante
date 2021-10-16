package com.example.montanteapp.Activities;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.montanteapp.Fragments.ShowRivalIListFragment;
import com.example.montanteapp.Fragments.ShowRivalMeListFragment;
import com.example.montanteapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RivalsActivity extends AppCompatActivity
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rivals);
        mBack = (Button) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
                return;
            }
        });

        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ShowRivalMeListFragment rivalsChallengedMe = new ShowRivalMeListFragment();
        ShowRivalIListFragment rivalsChallengedI = new ShowRivalIListFragment();
        adapter.addFragment(rivalsChallengedMe, "Назначенные мне");
        adapter.addFragment(rivalsChallengedI, "Назначенные мной");

        //rivalsChallengedI = new ShowRivalsListFragment().newInstance(getDataSetRivalsWhoChallengedI());
        //adapter.addFragment(rivalsChallengedI, "Назначенные мной");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


    }

class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) { super(manager); }

        @Override
        public Fragment getItem(int position) { return mFragmentList.get(position); }

        @Override
        public int getCount() { return mFragmentList.size(); }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) { return mFragmentTitleList.get(position); }
    }
}
