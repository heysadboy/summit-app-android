package org.openstack.android.summit.dagger.modules;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.INavigationParametersStore;
import org.openstack.android.summit.common.api.ISummitSelector;
import org.openstack.android.summit.common.data_access.repositories.IMemberDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitDataStore;
import org.openstack.android.summit.common.data_access.repositories.ISummitEventDataStore;
import org.openstack.android.summit.common.network.Reachability;
import org.openstack.android.summit.common.security.ISecurityManager;
import org.openstack.android.summit.modules.feedback_edit.FeedbackEditWireframe;
import org.openstack.android.summit.modules.feedback_edit.IFeedbackEditWireframe;
import org.openstack.android.summit.modules.feedback_edit.business_logic.FeedbackEditInteractor;
import org.openstack.android.summit.modules.feedback_edit.business_logic.IFeedbackEditInteractor;
import org.openstack.android.summit.modules.feedback_edit.user_interface.FeedbackEditFragment;
import org.openstack.android.summit.modules.feedback_edit.user_interface.FeedbackEditPresenter;
import org.openstack.android.summit.modules.feedback_edit.user_interface.IFeedbackEditPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Claudio Redi on 2/18/2016.
 */
@Module
public class FeedbackEditModule {
    @Provides
    FeedbackEditFragment providesFeedbackEditFragment() {
        return new FeedbackEditFragment();
    }

    @Provides
    IFeedbackEditWireframe providesFeedbackEditWireframe(INavigationParametersStore navigationParametersStore) {
        return new FeedbackEditWireframe(navigationParametersStore);
    }

    @Provides
    IFeedbackEditInteractor providesFeedbackEditInteractor(IMemberDataStore memberDataStore, ISummitEventDataStore summitEventDataStore, ISecurityManager securityManager, IDTOAssembler dtoAssembler, ISummitDataStore summitDataStore, ISummitSelector summitSelector) {
        return new FeedbackEditInteractor(memberDataStore, summitEventDataStore, securityManager, new Reachability(), dtoAssembler, summitDataStore, summitSelector);
    }

    @Provides
    IFeedbackEditPresenter providesFeedbackEditPresenter(IFeedbackEditInteractor feedbackEditInteractor, IFeedbackEditWireframe feedbackEditWireframe) {
        return new FeedbackEditPresenter(feedbackEditInteractor, feedbackEditWireframe);
    }    
}
