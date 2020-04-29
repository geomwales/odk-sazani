package org.sazani.collect.android.formentry.saving;

import android.net.Uri;

import org.sazani.collect.android.analytics.Analytics;
import org.sazani.collect.android.javarosawrapper.FormController;
import org.sazani.collect.android.tasks.SaveFormToDisk;
import org.sazani.collect.android.tasks.SaveToDiskResult;

public class DiskFormSaver implements FormSaver {

    @Override
    public SaveToDiskResult save(Uri instanceContentURI, FormController formController, boolean shouldFinalize, boolean exitAfter, String updatedSaveName, ProgressListener progressListener, Analytics analytics) {
        SaveFormToDisk saveFormToDisk = new SaveFormToDisk(formController, exitAfter, shouldFinalize, updatedSaveName, instanceContentURI, analytics);
        return saveFormToDisk.saveForm(progressListener);
    }
}
