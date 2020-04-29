package org.sazani.collect.android.formentry.saving;

import android.net.Uri;

import org.sazani.collect.android.analytics.Analytics;
import org.sazani.collect.android.javarosawrapper.FormController;
import org.sazani.collect.android.tasks.SaveToDiskResult;

public interface FormSaver {
    SaveToDiskResult save(Uri instanceContentURI, FormController formController, boolean shouldFinalize, boolean exitAfter, String updatedSaveName, ProgressListener progressListener, Analytics analytics);

    interface ProgressListener {
        void onProgressUpdate(String message);
    }
}
