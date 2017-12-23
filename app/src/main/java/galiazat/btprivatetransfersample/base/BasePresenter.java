package galiazat.btprivatetransfersample.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Azat on 27.11.17.
 */

public class BasePresenter<T extends BaseView, S extends BaseModel> {

    protected T view;
    protected S model;
    protected CompositeDisposable subscriptions;


    public BasePresenter(S model, T view) {
        this.view = view;
        this.model = model;
        this.subscriptions = new CompositeDisposable();
    }

    public void attachView(T view){
        this.view = view;
    }

    public void onCreate() {
        //you can implement it if you need it
    }

    public void onResume() {
        //you can implement it if you need it
    }


    public void onDestroy() {
        subscriptions.clear();
        view = null;
    }


    protected void add(Disposable disposable) {
        subscriptions.add(disposable);
    }



}
