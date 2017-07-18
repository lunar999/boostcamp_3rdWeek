package kyi.boost3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Kyu on 2017-07-18.
 */

public class BaseFragment extends Fragment {
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
            throw new IllegalStateException("cannot start fragment. " + fc.getName());
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }
}
