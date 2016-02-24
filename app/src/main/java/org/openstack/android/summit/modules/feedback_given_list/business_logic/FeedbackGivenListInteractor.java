package org.openstack.android.summit.modules.feedback_given_list.business_logic;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.DTOs.FeedbackDTO;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.security.ISecurityManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Claudio Redi on 1/27/2016.
 */
public class FeedbackGivenListInteractor extends BaseInteractor implements IFeedbackGivenListInteractor {
    ISecurityManager securityManager;

    public FeedbackGivenListInteractor(ISecurityManager securityManager, IDTOAssembler dtoAssembler, IDataUpdatePoller dataUpdatePoller) {
        super(dtoAssembler, dataUpdatePoller);
        this.securityManager = securityManager;
    }

    @Override
    public List<FeedbackDTO> getFeedbackGivenByCurrentUser() {
        Member member = securityManager.getCurrentMember();
        List<FeedbackDTO> dtos = createDTOList(member.getAttendeeRole().getFeedback(), FeedbackDTO.class);
        Collections.sort(dtos, new CustomComparator());
        return dtos;
    }

    public class CustomComparator implements Comparator<FeedbackDTO> {
        @Override
        public int compare(FeedbackDTO o1, FeedbackDTO o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}