package moezbenselem.campsite;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    private TextView mTextMessage;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_events:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, EventsFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_profile:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, ProfileFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_group:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, TeamFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_messages:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, MessagesFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_notif:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, NotificationFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
            }
            return false;
        }

    };


    public static SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);

        mAuth = FirebaseAuth.getInstance();

        try {
            fragmentManager.beginTransaction().replace(R.id.content, ProfileFragment.class.newInstance())
                    .addToBackStack(null).commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static MenuItem myActionMenuItem;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragmentManager.beginTransaction().replace(R.id.content, RechercheFragment.class.newInstance())
                            .addToBackStack(null).commit();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fragmentManager.beginTransaction().replace(R.id.content, RechercheFragment.class.newInstance())
                            .addToBackStack(null).commit();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                //UserFeedback.show( "SearchOnQueryTextSubmit: " + query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null)
            {
                mAuth.signOut();
                toStart();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toStart() {
        Intent toStart = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(toStart);
        finish();
    }

}
