package org.openstack.android.summit.common.security;

import android.app.Activity;
import org.openstack.android.summit.common.entities.Member;

/**
 * Created by Claudio Redi on 12/15/2015.
 */
public interface ISecurityManager {

    void init();

    void handleIllegalState();

    void login(Activity context);

    void logout(boolean enabledDataUpdates);

    boolean isLoggedIn();

    Member getCurrentMember();

    int getCurrentMemberId();

    boolean isLoggedInAndConfirmedAttendee();

    void bindCurrentUser();
}
