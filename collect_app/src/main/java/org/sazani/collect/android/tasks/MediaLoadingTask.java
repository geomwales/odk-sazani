package org.sazani.collect.android.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.sazani.collect.android.R;
import org.sazani.collect.android.activities.FormEntryActivity;
import org.sazani.collect.android.application.Collect;
import org.sazani.collect.android.dao.helpers.ContentResolverHelper;
import org.sazani.collect.android.exception.GDriveConnectionException;
import org.sazani.collect.android.formentry.ODKView;
import org.sazani.collect.android.fragments.dialogs.ProgressDialogFragment;
import org.sazani.collect.android.javarosawrapper.FormController;
import org.sazani.collect.android.network.NetworkStateProvider;
import org.sazani.collect.android.utilities.FileUtils;
import org.sazani.collect.android.utilities.ImageConverter;
import org.sazani.collect.android.utilities.MediaUtils;
import org.sazani.collect.android.utilities.ToastUtils;
import org.sazani.collect.android.widgets.BaseImageWidget;
import org.sazani.collect.android.widgets.QuestionWidget;

import java.io.File;
import java.lang.ref.WeakReference;

import timber.log.Timber;

public class MediaLoadingTask extends AsyncTask<Uri, Void, File> {

    private WeakReference<FormEntryActivity> formEntryActivity;
    private WeakReference<NetworkStateProvider> connectivityProvider;

    public MediaLoadingTask(FormEntryActivity formEntryActivity, NetworkStateProvider connectivityProvider) {
        onAttach(formEntryActivity);
        this.connectivityProvider = new WeakReference<>(connectivityProvider);
    }

    public void onAttach(FormEntryActivity formEntryActivity) {
        this.formEntryActivity = new WeakReference<>(formEntryActivity);
    }

    public void onDetach() {
        formEntryActivity = null;
        connectivityProvider = null;
    }

    @Override
    protected File doInBackground(Uri... uris) {

        File instanceFile;
        FormController formController = Collect.getInstance().getFormController();

        if (formController != null) {
            instanceFile = formController.getInstanceFile();
            if (instanceFile != null) {
                String instanceFolder = instanceFile.getParent();
                String extension = ContentResolverHelper.getFileExtensionFromUri(formEntryActivity.get(), uris[0]);
                String destMediaPath = instanceFolder + File.separator + System.currentTimeMillis() + extension;

                try {
                    File chosenFile = MediaUtils.getFileFromUri(formEntryActivity.get(), uris[0], MediaStore.Images.Media.DATA, connectivityProvider.get());
                    if (chosenFile != null) {
                        final File newFile = new File(destMediaPath);
                        FileUtils.copyFile(chosenFile, newFile);
                        QuestionWidget questionWidget = formEntryActivity.get().getWidgetWaitingForBinaryData();

                        // apply image conversion if the widget is an image widget
                        if (questionWidget instanceof BaseImageWidget) {
                            ImageConverter.execute(newFile.getPath(), questionWidget, formEntryActivity.get());
                        }

                        return newFile;
                    } else {
                        Timber.e("Could not receive chosen file");
                        formEntryActivity.get().runOnUiThread(() -> ToastUtils.showShortToastInMiddle(R.string.error_occured));
                        return null;
                    }
                } catch (GDriveConnectionException e) {
                    Timber.e("Could not receive chosen file due to connection problem");
                    formEntryActivity.get().runOnUiThread(() -> ToastUtils.showLongToastInMiddle(R.string.gdrive_connection_exception));
                    return null;
                }
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(File result) {
        Fragment prev = formEntryActivity.get().getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.COLLECT_PROGRESS_DIALOG_TAG);
        if (prev != null && !formEntryActivity.get().isInstanceStateSaved()) {
            ((DialogFragment) prev).dismiss();
        }

        ODKView odkView = formEntryActivity.get().getCurrentViewIfODKView();
        if (odkView != null) {
            odkView.setBinaryData(result);
        }
    }
}
