package org.openstack.android.summit.dagger.modules;

import android.content.Context;

import org.openstack.android.summit.common.ISession;
import org.openstack.android.summit.common.data_access.repositories.IMemberDataStore;
import org.openstack.android.summit.common.security.*;
import org.openstack.android.summit.common.security.SecurityManager;
import org.openstack.android.summit.common.security.oidc.IOIDCConfigurationManager;
import org.openstack.android.summit.common.security.oidc.OIDCConfigurationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 12/15/2015.
 */
@Module
public class SecurityModule {

    @Provides
    ITokenManagerFactory providesTokenManagerFactory(IOIDCConfigurationManager oidcConfigurationManager) {
        return new TokenManagerFactory(new TokenManagerOIDC(), new TokenManagerServiceAccount(oidcConfigurationManager));
    }

    @Provides
    @Singleton
    IPrincipalIdentity providesPrincipalIdentity(ISession session){
        return new PrincipalIdentity(session);
    }

    @Provides
    @Singleton
    ISecurityManager providesSecurityManager(IMemberDataStore memberDataStore, IPrincipalIdentity identity) {
        return new SecurityManager(new TokenManagerOIDC(), memberDataStore, identity);
    }

    @Provides
    @Singleton
    IOIDCConfigurationManager providesOIDCConfigurationManager(Context context) {
        return new OIDCConfigurationManager(new Base64Decoder(), new IConfigurationParamFinderStrategy[] {
                new ConfigurationParamMetadataFinderStrategy(context),
                new ConfigurationParamSafeStorageFinderStrategy()
        });
    }

    @Provides
    @Singleton
    IConfigurationParamsManager providesConfigurationManager(Context context) {
        return new ConfigurationParamsManager(new IConfigurationParamFinderStrategy[] {
                new ConfigurationParamMetadataFinderStrategy(context),
                new ConfigurationParamSafeStorageFinderStrategy()
        });
    }
}
