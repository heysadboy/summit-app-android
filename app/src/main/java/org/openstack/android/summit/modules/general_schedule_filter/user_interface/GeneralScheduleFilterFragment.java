package org.openstack.android.summit.modules.general_schedule_filter.user_interface;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.linearlistview.LinearListView;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.DTOs.NamedDTO;
import org.openstack.android.summit.common.DTOs.TrackGroupDTO;
import org.openstack.android.summit.common.user_interface.BaseFragment;

import java.util.List;

/**
 * Created by Claudio Redi on 2/1/2016.
 */
public class GeneralScheduleFilterFragment
        extends BaseFragment<IGeneralScheduleFilterPresenter>
        implements IGeneralScheduleFilterView {

    private SummitTypeListAdapter summitTypeListAdapter;
    private TrackGroupListAdapter trackGroupListAdapter;
    private EventTypeListAdapter eventTypeListAdapter;
    private LevelListAdapter levelListAdapter;
    private VenueListAdapter venueListAdapter;
    private ArrayAdapter<String> tagsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_general_schedule_filter, container, false);

        LinearListView summitTypesList = (LinearListView) view.findViewById(R.id.filter_summit_types_list);
        summitTypeListAdapter = new SummitTypeListAdapter(getContext());
        summitTypesList.setAdapter(summitTypeListAdapter);
        summitTypesList.setOnItemClickListener((parent, view1, position, id) -> {
            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(view1);
            presenter.toggleSelectionSummitType(generalScheduleFilterItemView, position);
        });

        final Switch hidePastTalks = (Switch) view.findViewById(R.id.hide_past_talks);
        hidePastTalks.setOnClickListener(v -> presenter.toggleHidePastTalks(hidePastTalks.isChecked()));

        LinearListView trackGroupList = (LinearListView) view.findViewById(R.id.filter_track_groups_list);
        trackGroupListAdapter = new TrackGroupListAdapter(getContext());
        trackGroupList.setAdapter(trackGroupListAdapter);

        LinearListView venuesList = (LinearListView) view.findViewById(R.id.filter_venues_list);
        venueListAdapter = new VenueListAdapter(getContext());
        venuesList.setAdapter(venueListAdapter);

        LinearListView levelList = (LinearListView) view.findViewById(R.id.filter_levels_list);
        levelListAdapter = new LevelListAdapter(getContext());
        levelList.setAdapter(levelListAdapter);

        super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void showSummitTypes(List<NamedDTO> summitTypes) {
        summitTypeListAdapter.clear();
        summitTypeListAdapter.addAll(summitTypes);
    }

    @Override
    public void toggleShowPastTalks(boolean isChecked) {
        Switch hidePastTalks = (Switch) view.findViewById(R.id.hide_past_talks);
        hidePastTalks.setChecked(isChecked);
    }

    @Override
    public void showShowPastTalks(boolean show) {
        LinearLayout header = (LinearLayout) view.findViewById(R.id.hide_past_talks_header);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.hide_past_talks_container);
        header.setVisibility(show ? View.VISIBLE : View.GONE);
        container.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
        setTitle(getResources().getString(R.string.filter));
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        presenter.onSaveInstanceState(outState);
    }


    @Override
    public void showTrackGroups(List<TrackGroupDTO> trackGroups) {
        trackGroupListAdapter.clear();
        trackGroupListAdapter.addAll(trackGroups);
    }

    @Override
    public void showEventTypes(List<NamedDTO> eventTypes) {
        eventTypeListAdapter.clear();
        eventTypeListAdapter.addAll(eventTypes);
    }

    @Override
    public void showVenues(List<NamedDTO> venues) {
        venueListAdapter.clear();
        venueListAdapter.addAll(venues);
    }

    @Override
    public void showLevels(List<String> levels) {
        levelListAdapter.clear();
        levelListAdapter.addAll(levels);
    }


    private class SummitTypeListAdapter extends ArrayAdapter<NamedDTO> {

        public SummitTypeListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_filter_list, parent, false);
            }

            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(convertView);
            presenter.buildSummitTypeFilterItem(generalScheduleFilterItemView, position);
            generalScheduleFilterItemView.setItemCallback(isChecked -> {
                presenter.toggleSelectionSummitType(generalScheduleFilterItemView, position);
            });
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class EventTypeListAdapter extends ArrayAdapter<NamedDTO> {

        public EventTypeListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_filter_list, parent, false);
            }

            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(convertView);
            presenter.buildEventTypeFilterItem(generalScheduleFilterItemView, position);

            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class VenueListAdapter extends ArrayAdapter<NamedDTO> {

        public VenueListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_filter_list, parent, false);
            }

            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(convertView);
            presenter.buildVenueFilterItem(generalScheduleFilterItemView, position);
            generalScheduleFilterItemView.setItemCallback(isChecked -> {
                presenter.toggleSelectionVenue(generalScheduleFilterItemView, position);
            });
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class TrackGroupListAdapter extends ArrayAdapter<TrackGroupDTO> {

        public TrackGroupListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_filter_list, parent, false);
            }

            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(convertView);
            presenter.buildTrackGroupFilterItem(generalScheduleFilterItemView, position);
            generalScheduleFilterItemView.setItemCallback(isChecked -> {
                presenter.toggleSelectionTrackGroup(generalScheduleFilterItemView, position);
            });
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class LevelListAdapter extends ArrayAdapter<String> {

        public LevelListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_filter_list, parent, false);
            }

            GeneralScheduleFilterItemView generalScheduleFilterItemView = new GeneralScheduleFilterItemView(convertView);
            presenter.buildLevelFilterItem(generalScheduleFilterItemView, position);
            generalScheduleFilterItemView.setItemCallback(isChecked -> {
                presenter.toggleSelectionLevel(generalScheduleFilterItemView, position);
            });
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }
}