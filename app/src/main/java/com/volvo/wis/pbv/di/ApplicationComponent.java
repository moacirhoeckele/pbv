package com.volvo.wis.pbv.di;

import com.volvo.wis.pbv.activities.KitActivity;
import com.volvo.wis.pbv.activities.LoginActivity;
import com.volvo.wis.pbv.activities.PickingActivity;

import javax.inject.Singleton;

import dagger.Component;

// The "modules" attribute in the @Component annotation tells Dagger what Modules
// to include when building the graph
@Singleton
@Component(modules = { ServicesModule.class })
public interface ApplicationComponent {

    // This tells Dagger that Activities request injection so the graph needs to
    // satisfy all the dependencies of the fields that LoginActivity is injecting.
    void inject(LoginActivity activity);

    void inject(KitActivity activity);

    void inject(PickingActivity activity);
}
