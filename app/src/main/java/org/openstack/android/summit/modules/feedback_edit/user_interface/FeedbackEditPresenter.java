package org.openstack.android.summit.modules.feedback_edit.user_interface;

import android.os.Bundle;

import org.openstack.android.summit.common.Constants;
import org.openstack.android.summit.common.entities.Feedback;
import org.openstack.android.summit.common.entities.exceptions.ValidationException;
import org.openstack.android.summit.common.user_interface.AlertsBuilder;
import org.openstack.android.summit.common.user_interface.BasePresenter;
import org.openstack.android.summit.modules.feedback_edit.IFeedbackEditWireframe;
import org.openstack.android.summit.modules.feedback_edit.business_logic.IFeedbackEditInteractor;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Claudio Redi on 2/17/2016.
 */
public class FeedbackEditPresenter
        extends BasePresenter<IFeedbackEditView, IFeedbackEditInteractor, IFeedbackEditWireframe>
        implements IFeedbackEditPresenter {

    private Integer eventId;
    private String  eventName;
    private Integer rate;

    public FeedbackEditPresenter(IFeedbackEditInteractor interactor, IFeedbackEditWireframe wireframe) {
        super(interactor, wireframe);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (savedInstanceState != null) ?
        savedInstanceState.getInt(Constants.NAVIGATION_PARAMETER_EVENT_ID, 0):
        wireframe.getParameter(Constants.NAVIGATION_PARAMETER_EVENT_ID, Integer.class);

        eventName = (savedInstanceState != null) ?
                savedInstanceState.getString(Constants.NAVIGATION_PARAMETER_EVENT_NAME, null):
                wireframe.getParameter(Constants.NAVIGATION_PARAMETER_EVENT_NAME, String.class);

        rate = (savedInstanceState != null) ?
                savedInstanceState.getInt(Constants.NAVIGATION_PARAMETER_EVENT_RATE, 0):
                wireframe.getParameter(Constants.NAVIGATION_PARAMETER_EVENT_RATE, Integer.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (eventId != null) {
            try {
                Feedback feedback = interactor.getFeedback(eventId);
                if (feedback != null) {
                    view.setRate(feedback.getRate());
                    view.setReview(feedback.getReview());
                } else {
                    view.setRate(rate);
                }
            }
            catch (ValidationException ex){
                view.hideActivityIndicator();
                AlertsBuilder.buildValidationError(view.getFragmentActivity() ,ex.getMessage()).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(eventId != null)
            outState.putInt(Constants.NAVIGATION_PARAMETER_EVENT_ID, eventId);

        if(rate != null)
            outState.putInt(Constants.NAVIGATION_PARAMETER_EVENT_RATE, eventId);
    }

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        if(eventName != null && !eventName.trim().isEmpty()) {
            view.setEventName(eventName);
        }
    }

    @Override
    public void saveFeedback() {

        view.showActivityIndicator();

        try {

            interactor
                    .saveFeedback(eventId, view.getRate(), view.getReview())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (data) -> {
                                view.hideActivityIndicator();
                                wireframe.backToPreviousView(view);
                            },
                            (ex) -> {
                                view.hideActivityIndicator();
                                AlertsBuilder.buildGenericError(view.getFragmentActivity()).show();
                            }
                    );
        }
        catch (ValidationException ex){
            view.hideActivityIndicator();
            AlertsBuilder.buildValidationError(view.getFragmentActivity() ,ex.getMessage()).show();
        }
    }
}