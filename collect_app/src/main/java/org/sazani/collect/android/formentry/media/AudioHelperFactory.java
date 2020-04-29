package org.sazani.collect.android.formentry.media;

import android.content.Context;

import org.sazani.collect.android.audio.AudioHelper;

public interface AudioHelperFactory {

    AudioHelper create(Context context);
}
