package com.survivingwithandroid.actionbartabnavigation.tasksscheduling;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class DeviceSettingsActivity extends Activity {

    public static final int MSG_UNCOLOUR_START = 0;
    public static final int MSG_UNCOLOUR_STOP = 1;
    public static final int MSG_SERVICE_OBJ = 2;
    int defaultColor;
    int startJobColor;
    int stopJobColor;
    private EditText mDelayEditText;
    private EditText mDeadlineEditText;
    private RadioButton mWiFiConnectivityRadioButton;
    private RadioButton mAnyConnectivityRadioButton;
    private CheckBox mRequiresChargingCheckBox;
    private CheckBox mRequiresIdleCheckbox;
    ComponentName serviceComponent;
    private static int currentJobId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Resources resources = getResources();

        defaultColor = resources.getColor(android.R.color.holo_blue_light);
        startJobColor = resources.getColor(android.R.color.holo_green_light);
        stopJobColor = resources.getColor(android.R.color.holo_red_light);

        mDelayEditText = (EditText) findViewById(R.id.etDelayTime);
        mDeadlineEditText = (EditText) findViewById(R.id.etDeadlineTime);
        mWiFiConnectivityRadioButton = (RadioButton) findViewById(R.id.cbUnmetered);
        mAnyConnectivityRadioButton = (RadioButton) findViewById(R.id.cbAny);
        mRequiresChargingCheckBox = (CheckBox) findViewById(R.id.cbCharging);
        mRequiresIdleCheckbox = (CheckBox) findViewById(R.id.cbIdle);
        serviceComponent = new ComponentName(this, JobScheduleService.class);
    }

    public void onClick(View view) {

        JobScheduler jobScheduler =
                (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (view.getId() == R.id.btnShedule) {

            JobInfo.Builder builder = new JobInfo.Builder(currentJobId++, serviceComponent);
            String delay = mDelayEditText.getText().toString();
            String deadline = mDeadlineEditText.getText().toString();
            boolean requiresUnmetered = mWiFiConnectivityRadioButton.isChecked();
            boolean requiresAnyConnectivity = mAnyConnectivityRadioButton.isChecked();

            if (delay != null && !TextUtils.isEmpty(delay)) {

                builder.setMinimumLatency(Long.valueOf(delay) * 1000);
            }

            if (deadline != null && !TextUtils.isEmpty(deadline)) {

                builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
            }

            if (requiresUnmetered) {

                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);

            } else if (requiresAnyConnectivity) {

                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            }

            builder.setRequiresDeviceIdle(mRequiresIdleCheckbox.isChecked());
            builder.setRequiresCharging(mRequiresChargingCheckBox.isChecked());

            jobScheduler.schedule(builder.build());

        } else {

            jobScheduler.cancelAll();
        }
    }
}
