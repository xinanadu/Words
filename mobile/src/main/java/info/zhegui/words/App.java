package info.zhegui.words;

import android.app.Application;

/**
 * Created by ASUS on 2014/11/17.
 */
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        ExceptionHandler customException = ExceptionHandler.getInstance();
        customException.init(getApplicationContext());
    }
}
