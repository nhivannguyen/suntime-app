package swin.android.suntime.ui;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import swin.android.suntime.R;
import swin.android.suntime.calc.AstronomicalCalendar;
import swin.android.suntime.calc.GeoLocation;

public class TimeTableFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    OnFragmentInteractionListener mListener;
    private Map.Entry<String, List<String>> ARG_PLACE;
    public TimeTableFragment() {
        // Required empty public constructor
    }

    public void setSelectedLocation(Map.Entry<String, List<String>> selectedLocation){
        ARG_PLACE = selectedLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.time_table_fragment, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(final View v, Bundle b){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd;

        dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.show(getActivity().getFragmentManager(),"tag");
    }

    public void calculateSuntime(ArrayList<Date> dateRange){
        String loca = "At " + ARG_PLACE.getKey().toUpperCase();
        String dates = "";
        String rises = "";
        String sets = "";
        TextView locData = (TextView) getActivity().findViewById(R.id.location_Caption);
        TextView dateData = (TextView) getActivity().findViewById((R.id.date_Data));
        TextView riseData = (TextView) getActivity().findViewById(R.id.srise_Data);
        TextView setData = (TextView) getActivity().findViewById(R.id.sset_Data);
        TimeZone tz = TimeZone.getTimeZone(ARG_PLACE.getValue().get(2));
        GeoLocation geolocation = new GeoLocation(ARG_PLACE.getKey(), Double.parseDouble(ARG_PLACE.getValue().get(0)), Double.parseDouble(ARG_PLACE.getValue().get(1)), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        for (Date d : dateRange) {
            ac.getCalendar().setTime(d);
            Date srise = ac.getSunrise();
            Date sset = ac.getSunset();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
            java.text.SimpleDateFormat sdfDay = new java.text.SimpleDateFormat("dd MMM yy");
            dates += sdfDay.format(d) + "\n";
            rises += sdf.format(srise) + "\n";
            sets += sdf.format(sset) + "\n";
        }
        locData.setText(loca);
        dateData.setText(dates);
        riseData.setText(rises);
        setData.setText(sets);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        ArrayList<Date> dates = new ArrayList<Date>();
        String dateString1 = year + "-" + monthOfYear + "-" + dayOfMonth;
        String dateString2 = yearEnd + "-" + monthOfYearEnd + "-" + dayOfMonthEnd;
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }

        calculateSuntime(dates);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

