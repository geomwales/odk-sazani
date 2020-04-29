package org.sazani.collect.android.regression;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sazani.collect.android.support.pages.AdminSettingsPage;
import org.sazani.collect.android.support.pages.MainMenuPage;

//Issue NODK-239
@RunWith(AndroidJUnit4.class)
public class AdminSettingsTest extends BaseRegressionTest {

    @Test
    public void when_openAdminSettings_should_notCrash() {
        //TestCase1
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .assertOnPage();
    }

    @Test
    public void when_rotateOnAdminSettingsView_should_notCrash() {
        //TestCase2
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .assertOnPage()
                .rotateToLandscape(new AdminSettingsPage(rule))
                .assertOnPage()
                .rotateToPortrait(new AdminSettingsPage(rule))
                .assertOnPage();
    }

}
