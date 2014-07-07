package knaps.hacker.school;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import knaps.hacker.school.adapters.StudentAdapter;
import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.HackerSchoolContentProvider;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.Constants;

/**
 * A fragment that shows a list view of all Hacker Schoolers.
 * <p/>
 * Still need to figure out how this works with a background loader
 * of all the hacker school batches
 */
public class HSListFragment extends Fragment implements
                                             LoaderManager.LoaderCallbacks<Cursor>,
                                             AdapterView.OnItemClickListener {

    private ListView mListView;
    private StudentAdapter mAdapter;
    private View mEmptyView;
    private View mLoadingView;
    private String mFilter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);
        mLoadingView = view.findViewById(R.id.loading_view);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new StudentAdapter(getActivity(), null, 0);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setItemsCanFocus(false);
        mListView.setFocusableInTouchMode(false);
        mListView.setClipToPadding(false);
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        getActivity().getLoaderManager().initLoader(0, savedInstanceState, this);
    }

    private BroadcastReceiver mLoadingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent != null) {
                if (Constants.ACTION_LOADING_START.equals(intent.getAction())) {
                    if (mEmptyView != null) mEmptyView.setVisibility(View.VISIBLE);
                }
                else if (Constants.ACTION_LOADING_ENDED.equals(intent.getAction())) {
                    if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        IntentFilter filter = new IntentFilter(Constants.ACTION_LOADING_START);
        filter.addAction(Constants.ACTION_LOADING_ENDED);
        LocalBroadcastManager.getInstance(activity).registerReceiver(mLoadingReceiver, filter);
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLoadingReceiver);
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filter", mFilter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (args != null) {
            String filter = "%" + args.getString("filter", "") + "%";
            selection = HSData.HSer.SELECTION_NAME_SKILLS;
            selectionArgs = new String[] {filter, filter, filter};
        }

        return new CursorLoader(getActivity(),
                HackerSchoolContentProvider.Uris.STUDENTS.getUri(),
                HSData.HSer.PROJECTION_ALL_BATCH,
                selection,
                selectionArgs,
                HSData.HSer.SORT_DEFAULT);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        mAdapter.swapCursor(data);
        if (data == null || data.getCount() <= 0) {
            showEmpty();
        }
        else {
            showList();
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void showEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    private void showList() {
        mEmptyView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Student student = mAdapter.getItem(position);
        final Intent intent = new Intent(getActivity(), HSProfileActivity.class);
        intent.putExtra(Constants.STUDENT, student);
        startActivity(intent);
    }

    public void filterSearch(final String currentFilter) {
        if (getActivity() != null) {
            mFilter = currentFilter;
            Bundle args = new Bundle();
            args.putString("filter", currentFilter);
            getLoaderManager().restartLoader(0, args, this);
        }
    }
}
