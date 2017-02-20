package org.openstack.android.summit.modules.splash.business_logic;

import android.app.Activity;
import android.content.Context;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.security.ISecurityManager;

/**
 * Created by smarcet on 2/6/17.
 */

public class SplashInteractor extends BaseInteractor implements ISplashInteractor {

    public SplashInteractor
    (
        ISummitDataStore summitDataStore,
        ISecurityManager securityManager,
        IDTOAssembler dtoAssembler,
        ISummitSelector summitSelector
    )
    {
        super(securityManager, dtoAssembler, summitSelector, summitDataStore);
    }


    @Override
    public void login(Context context) {
        securityManager.login((Activity)context);
    }
}
