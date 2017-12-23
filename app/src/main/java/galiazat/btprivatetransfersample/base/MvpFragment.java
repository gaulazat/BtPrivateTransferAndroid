package galiazat.btprivatetransfersample.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Azat on 27.11.17.
 */

public abstract class MvpFragment<T extends BasePresenter> extends Fragment {

    protected T presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (presenter == null) {
            presenter = initPresenter();
        } else {
            attachPresenter();
        }
        return view;
    }

    protected abstract void attachPresenter();

    protected abstract T initPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }

}
