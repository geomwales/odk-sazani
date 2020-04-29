package org.sazani.collect.android.widgets;

import androidx.annotation.NonNull;

import org.javarosa.core.model.data.DecimalData;
import org.sazani.collect.android.formentry.questions.QuestionDetails;
import org.sazani.collect.android.widgets.base.RangeWidgetTest;

/**
 * @author James Knight
 */

public class RangeDecimalWidgetTest extends RangeWidgetTest<RangeDecimalWidget, DecimalData> {

    @NonNull
    @Override
    public RangeDecimalWidget createWidget() {
        return new RangeDecimalWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @NonNull
    @Override
    public DecimalData getNextAnswer() {
        return new DecimalData(random.nextDouble());
    }
}
