package knaps.hacker.school;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.networking.Constants;

/**
 * Created by lisaneigut on 17 Sep 2013.
 */
public class ChooseGameFragment extends DialogFragment implements View.OnClickListener {

    KeyMap[] limitCounts;
    KeyMap[] batches;
    private Spinner mBatchSpinner;
    private Spinner mCountSpinner;
    private OnChooseGameListener mListener;

    interface OnChooseGameListener {
        public void onChooseGame(String batch, int gameMax);
    }

    public ChooseGameFragment() {}

    public void setOnChooseGameListener(OnChooseGameListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final HashSet<String> existingBatches = new HSDatabaseHelper(getActivity()).getExistingBatchesByName();
        ArrayList<String> batchNames = new ArrayList<String>(existingBatches);
        Collections.sort(batchNames);

        batches = new KeyMap[batchNames.size() + 1];
        batches[0] = KeyMap.make(-1, Constants.BATCH_STRING);
        for (int i = 0; i < batchNames.size(); i++) {
           batches[i + 1] = KeyMap.make(i + 1, batchNames.get(i));
        }

        limitCounts = new KeyMap[] {
                KeyMap.make(Constants.INVALID_MIN,  Constants.RUNTIME_STRING),
                KeyMap.make(5, "o(1)"),
                KeyMap.make(10, "o(n)"),
                KeyMap.make(20, "o(n^2)"),
                KeyMap.make(50, "o(n^n)"),
                KeyMap.make(Constants.INVALID_MIN, "o(n!)"),

        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_choose, container, false);

        mBatchSpinner = (Spinner) view.findViewById(R.id.spinnerBatch);
        mBatchSpinner.setAdapter(new PairAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, batches));
        mBatchSpinner.setPrompt("Batch");
        mCountSpinner = (Spinner) view.findViewById(R.id.spinnerLimit);
        Pair<Integer, String>[] paris = null;
        mCountSpinner.setAdapter(new PairAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, limitCounts));
        mCountSpinner.setPrompt("Run Time");

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
            final KeyMap batchItem = (KeyMap) mBatchSpinner.getSelectedItem();

            if (mListener != null) {
                mListener.onChooseGame(batchItem.sValue, countItem.sKey);
            }
            this.dismissAllowingStateLoss();
        }
    }

    private class PairAdapter extends ArrayAdapter<KeyMap> {

        public PairAdapter(Context context, int resource, KeyMap[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                return new View(getContext());
            }

            if (convertView == null || convertView.findViewById(android.R.id.text1) == null) {
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

