package org.openstack.android.openstacksummit;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.openstack.android.summit.common.data_access.deserialization.DeserializerStorage;
import org.openstack.android.summit.common.data_access.deserialization.FeedbackDeserializer;
import org.openstack.android.summit.common.data_access.deserialization.IMemberDeserializer;
import org.openstack.android.summit.common.data_access.deserialization.MemberDeserializer;
import org.openstack.android.summit.common.data_access.deserialization.PersonDeserializer;
import org.openstack.android.summit.common.data_access.deserialization.PresentationSpeakerDeserializer;
import org.openstack.android.summit.common.data_access.deserialization.SummitAttendeeDeserializer;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.Member;
import org.openstack.android.summit.common.entities.Summit;
import org.openstack.android.summit.common.entities.SummitAttendee;
import org.openstack.android.summit.common.entities.SummitEvent;
import org.openstack.android.summit.common.utils.RealmFactory;
import org.openstack.android.summit.common.utils.Void;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeserializerTests  extends InstrumentationTestCase {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        File tempFolder = testFolder.newFolder("realmdata");
        Realm.setDefaultConfiguration( new RealmConfiguration.Builder(tempFolder)
                .deleteRealmIfMigrationNeeded()
                .build());
        // create summit and event

        RealmFactory.transaction(new RealmFactory.IRealmCallback<Void>() {
            @Override
            public Void callback(Realm session) throws Exception {
                Summit summit = new Summit();
                summit.setId(1);
                summit.setName("test summit");

                summit = session.copyToRealmOrUpdate(summit);

                SummitEvent event = new SummitEvent();
                event.setId(1);
                event.setName("test event");
                event.setSummit(summit);

                event = session.copyToRealmOrUpdate(event);

                return Void.getInstance();
            }
        });
    }

    @Test
    public void memberOverwritingTest(){

        final String json = "{\"id\":13867,\"first_name\":\"Sebastian\",\"last_name\":\"Marcet\",\"gender\":\"Male\",\"bio\":null,\"linked_in\":\"http:\\/\\/www.linkedin.com\\/in\\/smarcet\",\"irc\":null,\"twitter\":null,\"pic\":\"https:\\/\\/devbranch.openstack.org\\/profile_images\\/members\\/13867\",\"attendee\":{\"id\":5720,\"summit_hall_checked_in\":false,\"summit_hall_checked_in_date\":null,\"shared_contact_info\":false,\"member_id\":13867,\"schedule\":[],\"tickets\":[]},\"feedback\":[{\"id\":147,\"rate\":5,\"note\":\"\\\"great presentation! test feedback.  \\\\n\\\"\",\"created_date\":1472161995,\"event_id\":1,\"member_id\":13867,\"attendee_id\":5720}]}";
        try {
            RealmFactory.transaction(new RealmFactory.IRealmCallback<Void>() {
                @Override
                public Void callback(Realm session) throws Exception {

                    DeserializerStorage deserializerStorage = new DeserializerStorage();
                    SummitAttendeeDeserializer summitAttendeeDeserializer = new SummitAttendeeDeserializer(deserializerStorage);
                    PersonDeserializer personDeserializer = new PersonDeserializer();
                    FeedbackDeserializer feedbackDeserializer = new FeedbackDeserializer(deserializerStorage);
                    PresentationSpeakerDeserializer presentationSpeakerDeserializer = new PresentationSpeakerDeserializer(personDeserializer, deserializerStorage);

                    IMemberDeserializer memberDeserializer = new MemberDeserializer(personDeserializer, presentationSpeakerDeserializer, summitAttendeeDeserializer, feedbackDeserializer, deserializerStorage);


                    Member member = memberDeserializer.deserialize(json);
                    member       = session.copyToRealmOrUpdate(member);
                    return Void.getInstance();
                }
            });

            RealmFactory.transaction(new RealmFactory.IRealmCallback<Void>() {
                @Override
                public Void callback(Realm session) throws Exception {

                    DeserializerStorage deserializerStorage = new DeserializerStorage();
                    SummitAttendeeDeserializer summitAttendeeDeserializer = new SummitAttendeeDeserializer(deserializerStorage);
                    PersonDeserializer personDeserializer = new PersonDeserializer();
                    FeedbackDeserializer feedbackDeserializer = new FeedbackDeserializer(deserializerStorage);
                    PresentationSpeakerDeserializer presentationSpeakerDeserializer = new PresentationSpeakerDeserializer(personDeserializer, deserializerStorage);

                    IMemberDeserializer memberDeserializer = new MemberDeserializer(personDeserializer, presentationSpeakerDeserializer, summitAttendeeDeserializer, feedbackDeserializer, deserializerStorage);


                    Member member = memberDeserializer.deserialize(json);
                    member       = session.copyToRealmOrUpdate(member);
                    return Void.getInstance();
                }
            });

        }
        catch (Exception ex){

        }

        Member member = RealmFactory.getSession().where(Member.class).equalTo("id", 13867).findFirst();

        Assert.assertTrue(member.getFeedback().size() > 0);
    }


    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}