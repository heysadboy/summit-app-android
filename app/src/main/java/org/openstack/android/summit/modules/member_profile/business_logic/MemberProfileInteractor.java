package org.openstack.android.summit.modules.member_profile.business_logic;

import org.openstack.android.summit.common.DTOs.Assembler.IDTOAssembler;
import org.openstack.android.summit.common.DTOs.MemberDTO;
import org.openstack.android.summit.common.DTOs.PersonDTO;
import org.openstack.android.summit.common.business_logic.BaseInteractor;
import org.openstack.android.summit.common.data_access.IGenericDataStore;
import org.openstack.android.summit.common.data_access.IPresentationSpeakerDataStore;
import org.openstack.android.summit.common.data_access.data_polling.IDataUpdatePoller;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.PresentationSpeaker;
import org.openstack.android.summit.common.security.ISecurityManager;

/**
 * Created by Claudio Redi on 1/26/2016.
 */
public class MemberProfileInteractor extends BaseInteractor implements IMemberProfileInteractor {
    protected ISecurityManager securityManager;
    protected IGenericDataStore genericDataStore;

    public MemberProfileInteractor(IGenericDataStore genericDataStore, ISecurityManager securityManager, IDTOAssembler dtoAssembler, IDataUpdatePoller dataUpdatePoller) {
        super(dtoAssembler, dataUpdatePoller);
        this.securityManager = securityManager;
        this.genericDataStore = genericDataStore;
    }

    @Override
    public MemberDTO getCurrentMember() {
        Member member = securityManager.getCurrentMember();
        MemberDTO dto = null;
        if (member != null) {
            dto = dtoAssembler.createDTO(member, MemberDTO.class);
        }
        return dto;
    }

    @Override
    public PersonDTO getPresentationSpeaker(int speakerId) {
        PresentationSpeaker presentationSpeaker = genericDataStore.getByIdLocal(speakerId, PresentationSpeaker.class);
        PersonDTO dto = dtoAssembler.createDTO(presentationSpeaker, PersonDTO.class);
        return dto;
    }
}
