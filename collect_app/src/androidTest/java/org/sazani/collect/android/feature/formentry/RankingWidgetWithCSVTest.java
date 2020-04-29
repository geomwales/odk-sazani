package org.sazani.collect.android.feature.formentry;

import android.Manifest;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.sazani.collect.android.activities.FormEntryActivity;
import org.sazani.collect.android.support.CopyFormRule;
import org.sazani.collect.android.support.ResetStateRule;
import org.sazani.collect.android.support.pages.FormEntryPage;
import org.sazani.collect.android.support.FormLoadingUtils;

import java.util.Collections;

public class RankingWidgetWithCSVTest {

    private static final String TEST_FORM = "ranking_widget.xml";

    @Rule
    public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            )
            .around(new ResetStateRule())
            .around(new CopyFormRule(TEST_FORM, Collections.singletonList("fruits.csv")));

    @Rule
    public IntentsTestRule<FormEntryActivity> activityTestRule = FormLoadingUtils.getFormActivityTestRuleFor(TEST_FORM);

    @Test
    public void rankingWidget_shouldDisplayItemsFromSearchFunc() {
        new FormEntryPage("ranking_widget", activityTestRule)
                .clickRankingButton()
                .assertText("Mango", "Oranges", "Strawberries");
    }
}
