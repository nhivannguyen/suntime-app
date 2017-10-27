package swin.android.suntime.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by vnhip on 26-Oct-17.
 */

public class SetTimeFragment extends Fragment {
    private static final String FILENAME = "data.txt";
    private Spinner loc_spin;
    private LinkedHashMap<String, List<String>> list;
    private Map.Entry<String, List<String>> place;
    private Integer[] date;

    public SetTimeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.set_time_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        loc_spin = (Spinner) view.findViewById(R.id.spinner);
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

    private void LoadCSV(){
        list = new LinkedHashMap<>();
        date = new Integer[3];

        File f = new File(getActivity().getApplicationContext().getFilesDir(), FILENAME);
        if (!f.exists()){
            try {
                FileOutputStream file = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE); // TODO: is this being called everytime?
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream raw = getResources().openRawResource(R.raw.au_locations);
        String toFile = streamToString(raw);

        try {
            InputStream in = getActivity().openFileInput(FILENAME);
            String textInData = streamToString(in);
            if (textInData.length() < toFile.length()) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE));
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
            FileInputStream stream = getActivity().openFileInput(FILENAME);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(list.keySet()));
            loc_spin.setAdapter(adapter);
        }
        place = list.entrySet().iterator().next();
        scanner.close();
    }

    private void initializeUI()
    {
        DatePicker dp = (DatePicker) getActivity().findViewById(R.id.datePicker);
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

        TextView sunriseTV = (TextView) getActivity().findViewById(R.id.sunriseTimeTV);
        TextView sunsetTV = (TextView) getActivity().findViewById(R.id.sunsetTimeTV);

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
