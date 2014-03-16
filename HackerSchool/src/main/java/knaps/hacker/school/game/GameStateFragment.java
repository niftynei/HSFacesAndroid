package knaps.hacker.school.game;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


/**
 * Created by lisaneigut on 16 Mar 2014.
 */
public class GameStateFragment extends Fragment {

    private static final String TAG = "GameStateFragment";
    public static Cursor studentCursor;

    public GameStateFragment() {}

    public static GameStateFragment findOrCreateRetainFragment(FragmentManager fm) {
        GameStateFragment fragment = (GameStateFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new GameStateFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
