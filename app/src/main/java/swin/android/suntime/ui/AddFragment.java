package swin.android.suntime.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import swin.android.suntime.R;

public class AddFragment extends Fragment {

    private static final String LOC_FILENAME = "data.txt";

    public AddFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_fragment, container, false);
    }

    public void addNewLoc() {
        // get data
        EditText locText = (EditText) getActivity().findViewById(R.id.locText);
        String locStr = locText.getText().toString();
        EditText latNum = (EditText) getActivity().findViewById(R.id.latText);
        String latDou = latNum.getText().toString();
        EditText logNum = (EditText) getActivity().findViewById(R.id.logText);
        String logDou = logNum.getText().toString();
        EditText tzText = (EditText) getActivity().findViewById(R.id.tzText);
        String tzStr = tzText.getText().toString();
        // put data in format
        String toLoad = locStr + "," + latDou + "," + logDou + "," + tzStr + "\n";

        Log.i("TO LOAD", toLoad);

        // write all data to file
        try {
            FileOutputStream file = getActivity().openFileOutput(LOC_FILENAME, Context.MODE_APPEND);
            OutputStreamWriter writer = new OutputStreamWriter(file);
            writer.write(toLoad);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
