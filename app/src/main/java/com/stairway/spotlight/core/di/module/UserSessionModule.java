package com.stairway.spotlight.core.di.module;

import com.stairway.data.source.auth.UserSessionResult;
import com.stairway.spotlight.core.di.scope.UserSessionScope;
import com.stairway.data.manager.XMPPManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vidhun on 16/07/16.
 */
@Module
public class UserSessionModule {
    private UserSessionResult userSessionResult;
    public UserSessionModule(UserSessionResult userSessionResult) {
        this.userSessionResult = userSessionResult;
    }

    @Provides
    @UserSessionScope
    public UserSessionResult providesSession() {
        return userSessionResult;
    }

    @Provides
    @UserSessionScope
    public XMPPManager providesXMPPConnection() {
        XMPPManager connection = new XMPPManager(userSessionResult.getUserId(), userSessionResult.getAccessToken());
        return connection;
    }

}
