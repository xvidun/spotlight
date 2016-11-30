package com.stairway.spotlight.core.di.component;

import com.stairway.data.source.user.UserSessionResult;
import com.stairway.spotlight.core.di.module.DataModule;
import com.stairway.spotlight.core.di.module.UserSessionModule;
import com.stairway.spotlight.core.di.scope.UserSessionScope;
import com.stairway.data.config.XMPPManager;
import com.stairway.spotlight.screens.home.chats.di.ChatListViewComponent;
import com.stairway.spotlight.screens.home.chats.di.ChatListViewModule;
import com.stairway.spotlight.screens.home.contacts.di.ContactListViewComponent;
import com.stairway.spotlight.screens.home.contacts.di.ContactListViewModule;
import com.stairway.spotlight.screens.message.di.MessageComponent;
import com.stairway.spotlight.screens.message.di.MessageModule;

import dagger.Subcomponent;

/**
 * Created by vidhun on 19/07/16.
 */
@UserSessionScope
@Subcomponent(modules = {UserSessionModule.class, DataModule.class})
public interface UserSessionComponent {
    UserSessionResult getUserSession();
    XMPPManager getXMPPConnection();

    // Subcomponents
    ChatListViewComponent plus(ChatListViewModule chatListViewModule);
    MessageComponent plus(MessageModule messageModule);
    ContactListViewComponent plus(ContactListViewModule contactListViewModule);
}
