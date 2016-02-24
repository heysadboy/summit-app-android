package org.openstack.android.summit.modules.feedback_edit.business_logic;

import org.openstack.android.summit.OpenStackSummitApplication;
import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.DTOs.FeedbackDTO;
import org.openstack.android.summit.common.DTOs.MemberDTO;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.business_logic.IInteractorAsyncOperationListener;
import org.openstack.android.summit.common.data_access.IDataStoreOperationListener;
import org.openstack.android.summit.common.data_access.IGenericDataStore;
import org.openstack.android.summit.common.data_access.ISummitAttendeeDataStore;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.data_access.deserialization.DataStoreOperationListener;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.network.IReachability;
import org.openstack.android.summit.common.security.ISecurityManager;

import java.util.Date;

/**
 * Created by Claudio Redi on 2/17/2016.
 */
public class FeedbackEditInteractor extends BaseInteractor implements IFeedbackEditInteractor {
    ISummitAttendeeDataStore summitAttendeeDataStore;
    IGenericDataStore genericDataStore;
    ISecurityManager securityManager;
    IReachability reachability;

    public FeedbackEditInteractor(ISummitAttendeeDataStore summitAttendeeDataStore, IGenericDataStore genericDataStore, ISecurityManager securityManager, IReachability reachability, IDTOAssembler dtoAssembler, IDataUpdatePoller dataUpdatePoller) {
        super(dtoAssembler, dataUpdatePoller);
        this.summitAttendeeDataStore = summitAttendeeDataStore;
        this.securityManager = securityManager;
        this.genericDataStore = genericDataStore;
        this.reachability = reachability;
    }

    @Override
    public void saveFeedback(int rate, String review, int eventId, final IInteractorAsyncOperationListener<FeedbackDTO> interactorAsyncOperationListener) {
        String error;
        if (!reachability.isNetworkingAvailable(OpenStackSummitApplication.context)) {
            error = "Feedback can't be created, there is no connectivity";
            interactorAsyncOperationListener.onError(error);
            return;
        }

        error = validateFeedback(rate, review);
        if (error != null) {
            interactorAsyncOperationListener.onError(error);
            return;
        }

        Member member = securityManager.getCurrentMember();
        SummitEvent summitEvent = genericDataStore.getByIdLocal(eventId, SummitEvent.class);
        final Feedback feedback = new Feedback();
        feedback.setEvent(summitEvent);
        feedback.setOwner(member.getAttendeeRole());
        feedback.setRate(rate);
        feedback.setReview(review);
        feedback.setDate(new Date());

        IDataStoreOperationListener<Feedback> dataStoreOperationListener = new DataStoreOperationListener<Feedback>() {
            @Override
            public void onSuceedWithSingleData(Feedback data) {
                FeedbackDTO dto = dtoAssembler.createDTO(data, FeedbackDTO.class);
                interactorAsyncOperationListener.onSucceedWithData(dto);
            }

            @Override
            public void onError(String message) {
                super.onError(message);
                interactorAsyncOperationListener.onError(message);
            }
        };

        summitAttendeeDataStore.addFeedback(member.getAttendeeRole(), feedback, dataStoreOperationListener);
    }

    private String validateFeedback(int rate, String review) {
        String errorMessage = null;
        if (rate == 0) {
            errorMessage = "You must provide a rate using stars at the top";
        }
        else if (review.isEmpty()) {
            errorMessage = "You must provide a review";
        }
        else if (review.length() > 500) {
            errorMessage = "Review exceeded 500 characters limit";
        }
        return errorMessage;
    }
}