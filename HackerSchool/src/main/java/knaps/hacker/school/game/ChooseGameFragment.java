package knaps.hacker.school.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import knaps.hacker.school.R;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.models.Batch;
import knaps.hacker.school.utils.Constants;

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
        public void onChooseGame(long batchId, String batchName, long gameMax);
    }

    public ChooseGameFragment() {}

    public void setOnChooseGameListener(OnChooseGameListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<Batch> batchNames = new HSDatabaseHelper(getActivity()).getExistingBatches();
        batches = new KeyMap[batchNames.size() + 1];
        batches[0] = KeyMap.make(-1, Constants.BATCH_STRING);
        for (int i = 0; i < batchNames.size(); i++) {
            Batch batch = batchNames.get(i);
            batches[i + 1] = KeyMap.make(batch.id, batch.name);
        }

        limitCounts = new KeyMap[] {
                KeyMap.make(Constants.INVALID_MIN, Constants.RUNTIME_STRING),
                KeyMap.make(5, getActivity().getString(R.string.o_one)),
                KeyMap.make(10, getActivity().getString(R.string.o_n)),
                KeyMap.make(20, getActivity().getString(R.string.o_squared)),
                KeyMap.make(50, getActivity().getString(R.string.o_to_n)),
                KeyMap.make(Constants.INVALID_MIN, getActivity().getString(R.string.o_n_factorial)),

        };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_choose, container, false);

        mBatchSpinner = (Spinner) view.findViewById(R.id.spinnerBatch);
        mBatchSpinner.setAdapter( new PairAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, batches));
        mBatchSpinner.setPrompt("Batch");
        mCountSpinner = (Spinner) view.findViewById(R.id.spinnerLimit);
        mCountSpinner.setAdapter( new PairAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, limitCounts));
        mCountSpinner.setPrompt("Run Number");

        view.findViewById(R.id.buttonPlay).setOnClickListener(this);
        return view;
    }

    private static class KeyMap {
        long sKey;
        String sValue;

        public KeyMap(long key, String value) {
            sKey = key;
            sValue = value;
        }

        public static KeyMap make(long key, String value) {
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
            final KeyMap countItem = (KeyMap) mCountSpinner.getSelectedItem();
            final KeyMap batchItem = (KeyMap) mBatchSpinner.getSelectedItem();

            if (mListener != null) {
                mListener.onChooseGame(batchItem.sKey, batchItem.sValue, countItem.sKey);
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

