package org.openstack.android.summit.modules.settings.user_interface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.openstack.android.summit.R;
import org.openstack.android.summit.common.user_interface.BaseFragment;

/**
 * Created by sebastian on 9/19/2016.
 */
public class SettingsFragment
        extends BaseFragment<ISettingsPresenter>
        implements ISettingsView {

    private Switch switchBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);
        presenter.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        this.view = view;
        switchBtn = (Switch) view.findViewById(R.id.switch_enable_notifications);
        super.onCreateView(inflater, container, savedInstanceState);
        presenter.onCreateView(savedInstanceState);

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                presenter.setBlockAllNotifications(isChecked);
            }
        });

        switchBtn.setChecked(presenter.getBlockAllNotifications());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
        setTitle(getResources().getString(R.string.Settings));
    }

}