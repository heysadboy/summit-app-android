package org.openstack.android.summit.common.utils;

/**
 * Created by sebastian on 8/2/2016.
 */
public class DeepLinkInfo {

    public static final String ActionViewEvent    = "VIEW_EVENT";
    public static final String ActionViewSpeaker  = "VIEW_SPEAKER";
    public static final String ActionViewLocation = "VIEW_LOCATION";
    public static final String ActionViewSchedule = "VIEW_SCHEDULE";

    public static final String EventsPath         = "events";
    public static final String SpeakersPath       = "speakers";
    public static final String LocationsPath      = "locations";
    public static final String SchedulePath       = "schedule";

    private String param;
    private String action;

    public DeepLinkInfo(String action, String param) {
        this.param  = param;
        if(action.toLowerCase().contains(EventsPath))      this.action = ActionViewEvent;
        if(action.toLowerCase().contains(SpeakersPath))    this.action = ActionViewSpeaker;
        if(action.toLowerCase().startsWith(LocationsPath)) this.action = ActionViewLocation;
        if(action.toLowerCase().startsWith(SchedulePath))  this.action = ActionViewSchedule;
    }

    public String getParam() {
        return param;
    }

    public boolean hasParam(){
        return !param.isEmpty();
    }

    public int getParamAsInt() {
        return Integer.parseInt(param);
    }

    public String getAction() {
        return action;
    }
}
