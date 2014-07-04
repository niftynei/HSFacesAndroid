package knaps.hacker.school;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
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
 *
 * Still need to figure out how this works with a background loader
 * of all the hacker school batches
 */
public class HSListFragment extends Fragment implements
                                             LoaderManager.LoaderCallbacks<Cursor>,
                                             AdapterView.OnItemClickListener {

    private ListView mListView;
    private StudentAdapter mAdapter;
    private String mCurrentFilter = ""; // currently always empty. TODO: implement search. Somewhere.

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
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

        getActivity().getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(mCurrentFilter)) {
            String filter = "%" + mCurrentFilter + "%";
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
        if (data != null && data.getCount() > 0) {
            mAdapter.swapCursor(data);
        }
        else {
            showEmpty();
        }
    }
    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void showEmpty() {
        // TODO: Show an empty screen
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Student student = mAdapter.getItem(position);
        final Intent intent = new Intent(getActivity(), HSProfileActivity.class);
        intent.putExtra(Constants.STUDENT, student);
        startActivity(intent);
    }
}
