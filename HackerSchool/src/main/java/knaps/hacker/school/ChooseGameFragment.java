package knaps.hacker.school;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.Inflater;

import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.networking.Constants;

/**
 * Created by lisaneigut on 17 Sep 2013.
 */
public class ChooseGameFragment extends Fragment implements View.OnClickListener {

    ArrayList<String> batchNames;
    KeyMap[] limitCounts;
    private Spinner mBatchSpinner;
    private Spinner mCountSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final HashSet<String> existingBatches = new HSDatabaseHelper(getActivity()).getExistingBatchesByName();
        batchNames = new ArrayList<String>(existingBatches);

        limitCounts = new KeyMap[] {
                KeyMap.make(5, "o(1)"),
                KeyMap.make(10, "o(n)"),
                KeyMap.make(20, "o(n^2)"),
                KeyMap.make(50, "o(n^n)"),
                KeyMap.make(Integer.MAX_VALUE, "o(n!)"),

        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_choose, container, false);

        mBatchSpinner = (Spinner) view.findViewById(R.id.spinnerBatch);
        mBatchSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, batchNames));
        mCountSpinner = (Spinner) view.findViewById(R.id.spinnerLimit);
        Pair<Integer, String>[] paris = null;
        mCountSpinner.setAdapter(new PairAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, limitCounts));

        view.findViewById(R.id.buttonPlay).setOnClickListener(this);
        return view;
    }

    private static class KeyMap {
        int sKey;
        String sValue;

        public KeyMap(int key, String value) {
            sKey = key;
            sValue = value;
        }

        public static KeyMap make(int key, String value) {
            return new KeyMap(key, value);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonPlay) {
            final Intent intent = new Intent(getActivity(), GuessThatHSActivity.class);
            final KeyMap countItem = (KeyMap) mCountSpinner.getSelectedItem();
            intent.putExtra(Constants.GAME_MAX, countItem.sKey);
            intent.putExtra(Constants.BATCH_NAME, (String) mBatchSpinner.getSelectedItem());
            Log.d("XML-- batchcount?", intent.toUri(0));
            startActivity(intent);
        }
    }

    private class PairAdapter extends ArrayAdapter<KeyMap> {

        public PairAdapter(Context context, int resource, KeyMap[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }
            KeyMap item = getItem(position);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.sValue);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }
            KeyMap item = getItem(position);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.sValue);
            return convertView;
        }
    }
}

