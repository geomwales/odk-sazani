package org.sazani.collect.android.support;

import androidx.test.rule.ActivityTestRule;

import org.sazani.collect.android.activities.MainMenuActivity;
import org.sazani.collect.android.support.pages.MainMenuPage;

public class FeatureTestRule extends ActivityTestRule<MainMenuActivity> {

    public FeatureTestRule() {
        super(MainMenuActivity.class);
    }

    public MainMenuPage mainMenu() {
        return new MainMenuPage(this).assertOnPage();
    }
}
