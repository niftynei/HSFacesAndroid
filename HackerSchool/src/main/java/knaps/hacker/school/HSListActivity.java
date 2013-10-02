package knaps.hacker.school;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.SQLiteCursorLoader;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.utils.Constants;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HSListActivity extends BaseFragmentActivity implements
                                                         LoaderManager.LoaderCallbacks<Cursor>,
                                                         AdapterView.OnItemClickListener {

    SimpleCursorAdapter mAdapter;
    ListView mListView;
    private String mCurrentFilter = "";
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.list);

        String[] fromColumns = {HSData.HSer.COLUMN_NAME_FULL_NAME};
        int[] toViews = {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        handleIntent(getIntent());
        setupActionBar();
    }

    private void handleIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mCurrentFilter = intent.getStringExtra(SearchManager.QUERY);
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (AppUtil.isHoneycomb()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mCurrentFilter = intent.getStringExtra(SearchManager.QUERY).toLowerCase().trim();
        }

        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);

        if (AppUtil.isHoneycomb()) {
            final MenuItem searchItem = menu.findItem(R.id.search);
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setIconifiedByDefault(false);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    mCurrentFilter = query;
                    getSupportLoaderManager().restartLoader(0, null, HSListActivity.this);

                    if (AppUtil.isHoneycomb()) invalidateOptionsMenu();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    mCurrentFilter = newText;
                    getSupportLoaderManager().restartLoader(0, null, HSListActivity.this);
                    return true;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    searchItem.collapseActionView();
                    return true;
                }
            });
        }
        else {

        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (AppUtil.isIcs()) menu.findItem(R.id.search).collapseActionView();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.search:
                if (!AppUtil.isHoneycomb()) onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!TextUtils.isEmpty(mCurrentFilter)) {
            return new SQLiteCursorLoader(this,
                    HSData.HSer.GET_ALL_FILTERED,
                    new String[] {"%" + mCurrentFilter + "%", "%" + mCurrentFilter + "%"});
        }

        return new SQLiteCursorLoader(this,
                HSData.HSer.TABLE_NAME, HSData.HSer.PROJECTION_ALL,
                HSData.HSer.SORT_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor o) {
        mAdapter.swapCursor(o);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);
        final Student student = new Student(cursor);
        final Intent intent = new Intent(this, HSProfileActivity.class);
        intent.putExtra(Constants.STUDENT, student);
        startActivity(intent);
    }
}
