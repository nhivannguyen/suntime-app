package swin.android.suntime.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import swin.android.suntime.R;
import swin.android.suntime.calc.AstronomicalCalendar;
import swin.android.suntime.calc.GeoLocation;


public class Main extends AppCompatActivity
{

    private static final String FILENAME = "data.txt";
    private Spinner loc_spin;
    private LinkedHashMap<String, List<String>> list;
    private Map.Entry<String, List<String>> place;
    private Integer[] date;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LoadCSV();
        initializeUI();
        loc_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getSelectedItem().toString();
                for (Map.Entry<String, List<String>> entry : list.entrySet()) {
                    String city = entry.getKey();
                    if (selected.equals(city)) {
                        place = entry;
                    }
                }
                updateTime();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                AddFragment add = new AddFragment();
                add.addNewLoc();
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

    public void toSelect(View v) {
        Intent intent = new Intent(this, AddFragment.class);
        startActivity(intent);
    }

    private void LoadCSV(){
        list = new LinkedHashMap<>();
        loc_spin = (Spinner) findViewById(R.id.spinner);
        date = new Integer[3];


        File f = new File(getApplicationContext().getFilesDir(), FILENAME);
        if (!f.exists()){
            try {
                FileOutputStream file = openFileOutput(FILENAME, Context.MODE_PRIVATE); // TODO: is this being called everytime?
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream raw = getResources().openRawResource(R.raw.au_locations);
        String toFile = streamToString(raw);

        try {
            InputStream in = openFileInput(FILENAME);
            String textInData = streamToString(in);
            if (textInData.length() < toFile.length()) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));
                    writer.write(toFile);
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = "";
        try {
            FileInputStream stream = openFileInput(FILENAME);
            str = streamToString(stream);
            stream.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(str);
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            String line = scanner.next();
            String[] values = line.split(",");
            List<String> data = new ArrayList<>();
            for (int i=1;i<values.length;i++) {
                data.add(values[i]);
            }
            list.put(values[0], data);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(list.keySet()));
            loc_spin.setAdapter(adapter);
        }
        place = list.entrySet().iterator().next();
        scanner.close();
    }

    private void initializeUI()
    {
        DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
        Calendar cal = Calendar.getInstance();
        date[0] = cal.get(Calendar.YEAR);
        date[1] = cal.get(Calendar.MONTH);
        date[2] = cal.get(Calendar.DAY_OF_MONTH);
        dp.init(date[0],date[1],date[2],dateChangeHandler); // setup initial values and reg. handler
        updateTime();
    }

    private void updateTime(){
        TimeZone tz = TimeZone.getTimeZone(place.getValue().get(2));
        GeoLocation geolocation = new GeoLocation(place.getKey(), Double.parseDouble(place.getValue().get(0)), Double.parseDouble(place.getValue().get(1)), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        ac.getCalendar().set(date[0],date[1],date[2]);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        TextView sunriseTV = (TextView) findViewById(R.id.sunriseTimeTV);
        TextView sunsetTV = (TextView) findViewById(R.id.sunsetTimeTV);

        sunriseTV.setText(sdf.format(srise));
        sunsetTV.setText(sdf.format(sset));
    }

    DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener()
    {
        public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth)
        {
            date[0] = year;
            date[1] = monthOfYear;
            date[2] = dayOfMonth;
            updateTime();
        }
    };

    private static String streamToString(InputStream in) {
        String l;
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder s = new StringBuilder();
        try {
            while ((l = r.readLine()) != null) {
                s.append(l + "\n");
            }
        } catch (IOException e) {}
        return s.toString();
    }
}
