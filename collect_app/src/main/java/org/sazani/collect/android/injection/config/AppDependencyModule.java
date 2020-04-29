package org.sazani.collect.android.injection.config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.webkit.MimeTypeMap;

import org.javarosa.core.reference.ReferenceManager;
import org.sazani.collect.android.analytics.Analytics;
import org.sazani.collect.android.analytics.FirebaseAnalytics;
import org.sazani.collect.android.backgroundwork.CollectBackgroundWorkManager;
import org.sazani.collect.android.dao.FormsDao;
import org.sazani.collect.android.dao.InstancesDao;
import org.sazani.collect.android.events.RxEventBus;
import org.sazani.collect.android.formentry.media.AudioHelperFactory;
import org.sazani.collect.android.formentry.media.ScreenContextAudioHelperFactory;
import org.sazani.collect.android.geo.MapProvider;
import org.sazani.collect.android.jobs.CollectJobCreator;
import org.sazani.collect.android.metadata.InstallIDProvider;
import org.sazani.collect.android.metadata.SharedPreferencesInstallIDProvider;
import org.sazani.collect.android.network.NetworkStateProvider;
import org.sazani.collect.android.openrosa.CollectThenSystemContentTypeMapper;
import org.sazani.collect.android.openrosa.OpenRosaAPIClient;
import org.sazani.collect.android.openrosa.OpenRosaHttpInterface;
import org.sazani.collect.android.openrosa.okhttp.OkHttpConnection;
import org.sazani.collect.android.openrosa.okhttp.OkHttpOpenRosaServerClientProvider;
import org.sazani.collect.android.preferences.AdminSharedPreferences;
import org.sazani.collect.android.preferences.GeneralSharedPreferences;
import org.sazani.collect.android.preferences.MetaSharedPreferencesProvider;
import org.sazani.collect.android.storage.StoragePathProvider;
import org.sazani.collect.android.storage.StorageStateProvider;
import org.sazani.collect.android.storage.migration.StorageEraser;
import org.sazani.collect.android.storage.migration.StorageMigrationRepository;
import org.sazani.collect.android.storage.migration.StorageMigrator;
import org.sazani.collect.android.tasks.sms.SmsSubmissionManager;
import org.sazani.collect.android.tasks.sms.contracts.SmsSubmissionManagerContract;
import org.sazani.collect.android.utilities.ActivityAvailability;
import org.sazani.collect.android.utilities.AdminPasswordProvider;
import org.sazani.collect.android.utilities.AndroidUserAgent;
import org.sazani.collect.android.utilities.DeviceDetailsProvider;
import org.sazani.collect.android.utilities.FormListDownloader;
import org.sazani.collect.android.network.ConnectivityProvider;
import org.sazani.collect.android.utilities.PermissionUtils;
import org.sazani.collect.android.utilities.WebCredentialsUtils;
import org.sazani.collect.utilities.BackgroundWorkManager;
import org.sazani.collect.utilities.UserAgentProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

import static org.sazani.collect.android.preferences.GeneralKeys.KEY_INSTALL_ID;

/**
 * Add dependency providers here (annotated with @Provides)
 * for objects you need to inject
 */
@Module
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class AppDependencyModule {

    @Provides
    SmsManager provideSmsManager() {
        return SmsManager.getDefault();
    }

    @Provides
    SmsSubmissionManagerContract provideSmsSubmissionManager(Application application) {
        return new SmsSubmissionManager(application);
    }

    @Provides
    Context context(Application application) {
        return application;
    }

    @Provides
    public InstancesDao provideInstancesDao() {
        return new InstancesDao();
    }

    @Provides
    public FormsDao provideFormsDao() {
        return new FormsDao();
    }

    @Provides
    @Singleton
    RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    MimeTypeMap provideMimeTypeMap() {
        return MimeTypeMap.getSingleton();
    }

    @Provides
    @Singleton
    UserAgentProvider providesUserAgent() {
        return new AndroidUserAgent();
    }

    @Provides
    @Singleton
    OpenRosaHttpInterface provideHttpInterface(MimeTypeMap mimeTypeMap, UserAgentProvider userAgentProvider) {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientProvider(new OkHttpClient()),
                new CollectThenSystemContentTypeMapper(mimeTypeMap),
                userAgentProvider.getUserAgent()
        );
    }

    @Provides
    public OpenRosaAPIClient provideCollectServerClient(OpenRosaHttpInterface httpInterface, WebCredentialsUtils webCredentialsUtils) {
        return new OpenRosaAPIClient(httpInterface, webCredentialsUtils);
    }

    @Provides
    WebCredentialsUtils provideWebCredentials() {
        return new WebCredentialsUtils();
    }

    @Provides
    FormListDownloader provideDownloadFormListDownloader(
            Application application,
            OpenRosaAPIClient openRosaAPIClient,
            WebCredentialsUtils webCredentialsUtils,
            FormsDao formsDao) {
        return new FormListDownloader(
                application,
                openRosaAPIClient,
                webCredentialsUtils,
                formsDao
        );
    }

    @Provides
    @Singleton
    public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
        com.google.firebase.analytics.FirebaseAnalytics firebaseAnalyticsInstance = com.google.firebase.analytics.FirebaseAnalytics.getInstance(application);
        return new FirebaseAnalytics(firebaseAnalyticsInstance, generalSharedPreferences);
    }

    @Provides
    public PermissionUtils providesPermissionUtils() {
        return new PermissionUtils();
    }

    @Provides
    public ReferenceManager providesReferenceManager() {
        return ReferenceManager.instance();
    }

    @Provides
    public AudioHelperFactory providesAudioHelperFactory() {
        return new ScreenContextAudioHelperFactory();
    }

    @Provides
    public ActivityAvailability providesActivityAvailability(Context context) {
        return new ActivityAvailability(context);
    }

    @Provides
    @Singleton
    public StorageMigrationRepository providesStorageMigrationRepository() {
        return new StorageMigrationRepository();
    }

    @Provides
    StorageMigrator providesStorageMigrator(StoragePathProvider storagePathProvider, StorageStateProvider storageStateProvider, StorageMigrationRepository storageMigrationRepository, ReferenceManager referenceManager, BackgroundWorkManager backgroundWorkManager, Analytics analytics) {
        StorageEraser storageEraser = new StorageEraser(storagePathProvider);

        return new StorageMigrator(storagePathProvider, storageStateProvider, storageEraser, storageMigrationRepository, GeneralSharedPreferences.getInstance(), referenceManager, backgroundWorkManager, analytics);
    }

    @Provides
    InstallIDProvider providesInstallIDProvider(Context context) {
        SharedPreferences prefs = new MetaSharedPreferencesProvider(context).getMetaSharedPreferences();
        return new SharedPreferencesInstallIDProvider(prefs, KEY_INSTALL_ID);
    }

    @Provides
    public DeviceDetailsProvider providesDeviceDetailsProvider(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return new DeviceDetailsProvider() {

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getDeviceId() {
                return telMgr.getDeviceId();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getLine1Number() {
                return telMgr.getLine1Number();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getSubscriberId() {
                return telMgr.getSubscriberId();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getSimSerialNumber() {
                return telMgr.getSimSerialNumber();
            }
        };
    }

    @Provides
    @Singleton
    GeneralSharedPreferences providesGeneralSharedPreferences(Context context) {
        return new GeneralSharedPreferences(context);
    }

    @Provides
    @Singleton
    AdminSharedPreferences providesAdminSharedPreferences(Context context) {
        return new AdminSharedPreferences(context);
    }

    @Provides
    @Singleton
    public MapProvider providesMapProvider() {
        return new MapProvider();
    }

    @Provides
    public StorageStateProvider providesStorageStateProvider() {
        return new StorageStateProvider();
    }

    @Provides
    public StoragePathProvider providesStoragePathProvider() {
        return new StoragePathProvider();
    }

    @Provides
    public AdminPasswordProvider providesAdminPasswordProvider() {
        return new AdminPasswordProvider(AdminSharedPreferences.getInstance());
    }

    @Provides
    public CollectJobCreator providesCollectJobCreator() {
        return new CollectJobCreator();
    }

    @Provides
    public BackgroundWorkManager providesBackgroundWorkManager() {
        return new CollectBackgroundWorkManager();
    }

    @Provides
    public NetworkStateProvider providesConnectivityProvider() {
        return new ConnectivityProvider();
    }
}
