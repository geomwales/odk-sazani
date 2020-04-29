package org.sazani.collect.android.widgets;

import android.view.View;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.sazani.collect.android.formentry.questions.QuestionDetails;
import org.sazani.collect.android.widgets.base.GeneralSelectOneWidgetTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */

public class SpinnerWidgetTest extends GeneralSelectOneWidgetTest<SpinnerWidget> {
    @NonNull
    @Override
    public SpinnerWidget createWidget() {
        return new SpinnerWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), false);
    }

    @Test
    public void usingReadOnlyOptionShouldMakeAllClickableElementsDisabled() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);

        assertThat(getWidget().spinner.getVisibility(), is(View.VISIBLE));
        assertThat(getWidget().spinner.isEnabled(), is(Boolean.FALSE));
    }
}
