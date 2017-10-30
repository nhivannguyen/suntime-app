package swin.android.suntime.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;
import java.util.Map;

import swin.android.suntime.R;


public class Main extends AppCompatActivity implements SetTimeFragment.dataPass, TimeTableFragment.OnFragmentInteractionListener
{
    private String _shareData;
    private Map.Entry<String, List<String>> _location;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SetTimeFragment()).commit();
        getFragmentManager().beginTransaction().addToBackStack(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add_fragment items to action bar if exists
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selected){

        switch(selected.getItemId()){
            case R.id.add:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();
                getFragmentManager().beginTransaction().addToBackStack(null);
                return true;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, _shareData);
                startActivity(shareIntent);
                return true;
            case R.id.dates_select:
                TimeTableFragment fTime = new TimeTableFragment();
                fTime.setSelectedLocation(_location);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fTime).commit();
                getFragmentManager().beginTransaction().addToBackStack(null);
                return true;
            case R.id.exit:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(selected);
    }

    @Override
    public void shareData(Map.Entry<String, List<String>> place, String s) {
        _shareData = s;
        _location = place;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
