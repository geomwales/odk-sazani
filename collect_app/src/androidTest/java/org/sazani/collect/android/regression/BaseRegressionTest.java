package org.sazani.collect.android.regression;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.sazani.collect.android.activities.MainMenuActivity;

public class BaseRegressionTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> rule = new ActivityTestRule<>(MainMenuActivity.class);
}