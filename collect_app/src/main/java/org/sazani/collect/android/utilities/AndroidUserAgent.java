package org.sazani.collect.android.utilities;

import org.sazani.collect.android.BuildConfig;
import org.sazani.collect.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        return String.format("%s/%s %s",
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                System.getProperty("http.agent"));
    }

}
