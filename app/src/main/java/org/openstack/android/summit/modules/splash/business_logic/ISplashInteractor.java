package org.openstack.android.summit.modules.splash.business_logic;

import android.content.Context;

import org.openstack.android.summit.common.business_logic.IBaseInteractor;

/**
 * Created by smarcet on 2/6/17.
 */

public interface ISplashInteractor extends IBaseInteractor {

    boolean isMemberLogged();

    void login(Context context);
}