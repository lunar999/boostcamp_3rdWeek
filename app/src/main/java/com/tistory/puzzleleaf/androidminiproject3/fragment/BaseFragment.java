package com.tistory.puzzleleaf.androidminiproject3.fragment;

import android.support.v4.app.Fragment;
import com.tistory.puzzleleaf.androidminiproject3.R;

/**
 * Created by cmtyx on 2017-07-16.
 */
//http://www.masterqna.com/android/29512/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%ED%95%98%EC%9C%84-fragment-%EA%B0%84%EC%9D%98-%EC%9D%B4%EB%8F%99
//하위 프레그먼트간 이동 참고
public class BaseFragment extends Fragment {
    //모든 프레그먼트가 상속할 최상위 부모
    protected void startFragment(Class<? extends BaseFragment> fragmentClass){
        BaseFragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment == null) {
            throw new IllegalStateException("프레그먼트를 시작할 수 없습니다 : " + fragmentClass.getName());
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment,fragment).addToBackStack(null).commit();
    }
}
