package kyi.boost3;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Kyu on 2017-07-18.
 */

public class BaseFragment extends Fragment {
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor editor;
    protected MarkerData markers;

    protected void startFragment (FragmentManager fm, Class<? extends BaseFragment> fc) {
        BaseFragment fragment = null;
        try {
            fragment = fc.newInstance();
        } catch(java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        if(fragment == null) {
            throw new IllegalStateException(getResources().getString(R.string.fragment_illegal_state_exception) + fc.getName());
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }
}
