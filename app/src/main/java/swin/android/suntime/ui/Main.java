package swin.android.suntime.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import swin.android.suntime.R;


public class Main extends AppCompatActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SetTimeFragment()).commit();
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
                return true;
            case R.id.share:
                // something something
                return true;
            case R.id.exit:
                // exit the app
                return true;
        }

        return super.onOptionsItemSelected(selected);
    }
}
