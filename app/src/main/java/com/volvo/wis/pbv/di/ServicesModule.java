package com.volvo.wis.pbv.di;

import android.content.Context;
import android.support.annotation.NonNull;

import com.volvo.wis.pbv.services.AuthenticationService;
import com.volvo.wis.pbv.services.IAuthenticationService;
import com.volvo.wis.pbv.services.IPickingService;
import com.volvo.wis.pbv.services.PickingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

// @Module informs Dagger that this class is a Dagger Module
@Module
public class ServicesModule {

    private Context context;

    public ServicesModule(@NonNull Context context) {
        this.context = context;
    }

    // @Provides tell Dagger how to create instances of the type that this function.
    @Singleton
    @Provides
    @NonNull
    public Context provideContext(){
        return context;
    }

    @Singleton
    @Provides
    public IAuthenticationService provideAuthenticationService(Context context) {
        return new AuthenticationService(context);
    }

    @Singleton
    @Provides
    public IPickingService providePickingService(Context context) {
        return new PickingService(context);
    }
}
