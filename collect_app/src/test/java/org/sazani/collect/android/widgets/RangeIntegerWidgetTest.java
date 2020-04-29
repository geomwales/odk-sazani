package org.sazani.collect.android.widgets;

import androidx.annotation.NonNull;

import org.javarosa.core.model.data.IntegerData;
import org.sazani.collect.android.formentry.questions.QuestionDetails;
import org.sazani.collect.android.widgets.base.RangeWidgetTest;

/**
 * @author James Knight
 */
public class RangeIntegerWidgetTest extends RangeWidgetTest<RangeIntegerWidget, IntegerData> {

    @NonNull
    @Override
    public RangeIntegerWidget createWidget() {
        return new RangeIntegerWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @NonNull
    @Override
    public IntegerData getNextAnswer() {
        return new IntegerData(random.nextInt());
    }
}
