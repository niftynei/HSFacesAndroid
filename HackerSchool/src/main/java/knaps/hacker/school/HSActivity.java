package knaps.hacker.school;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import knaps.hacker.school.adapters.StudentAdapter;
import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.HackerSchoolContentProvider;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.HSOAuthService;
import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.utils.Constants;

public class HSActivity extends BaseFragmentActivity {

    private StudentAdapter mAdapter;
    private ListView mListView;
    private String mCurrentFilter = "";
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment;
        String name;
        if (!HSOAuthService.getService().isAuthorized()) {
            fragment = new LoginFragment();
            name = "login";
        }
        else {
            fragment = new HSListFragment();
            name = "list";
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(fragment, name);
        transaction.addToBackStack(name);
        transaction.commit();

        handleIntent(getIntent());
        setupActionBar();
    }

    private void handleIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mCurrentFilter = intent.getStringExtra(SearchManager.QUERY);
        }
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
        // TODO: move this to a new Search Activity
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mCurrentFilter = intent.getStringExtra(SearchManager.QUERY).toLowerCase().trim();
        }
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

                    if (AppUtil.isHoneycomb()) invalidateOptionsMenu();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    mCurrentFilter = newText;
                    return true;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

                @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                @Override
                public boolean onClose() {
                    searchItem.collapseActionView();
                    return true;
                }
            });
        }
        else {
            // fuck you gingerbread
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.search).collapseActionView();
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
}
