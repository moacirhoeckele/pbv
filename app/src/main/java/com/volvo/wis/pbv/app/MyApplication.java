package com.volvo.wis.pbv.app;

import android.app.Application;

import com.volvo.wis.pbv.di.ApplicationComponent;
import com.volvo.wis.pbv.di.DaggerApplicationComponent;
import com.volvo.wis.pbv.di.ServicesModule;

public class MyApplication extends Application {

    private static ApplicationComponent applicationComponent;

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void  onCreate(){
        super.onCreate();
        applicationComponent = buildComponent();
    }

    public ApplicationComponent buildComponent(){
        return DaggerApplicationComponent
                .builder()
                .servicesModule(new ServicesModule(this))
                .build();
    }
}

