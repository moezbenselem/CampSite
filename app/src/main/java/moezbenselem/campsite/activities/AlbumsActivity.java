package moezbenselem.campsite.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import moezbenselem.campsite.R;
import moezbenselem.campsite.fragments.ImagesFragment;
import moezbenselem.campsite.fragments.VideoFragment;

public class AlbumsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        tabLayout = findViewById(R.id.tabs_album);
        viewPager = findViewById(R.id.pager_album);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return new ImagesFragment();
                } else {
                    return new VideoFragment();
                }
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "IMAGES";
                } else {
                    return "VIDEOS";
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

    }


}