package org.sazani.collect.android.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.sazani.collect.android.R;
import org.sazani.collect.android.activities.CollectAbstractActivity;

public class FormMetadataPreferencesActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_layout);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_fragment_container, new FormMetadataFragment())
                .commit();
    }
}
