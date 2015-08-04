package com.survivingwithandroid.actionbartabnavigation.tasksscheduling;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class JobScheduleService extends JobService {

    private static final String TAG = "SyncService";
    private final LinkedList<JobParameters> jobParameterses = new LinkedList<JobParameters>();
    DeviceSettingsActivity activity;

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i(TAG, "on start job: " + params.getJobId());

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Log.i(TAG, "on stop job: " + params.getJobId());

        return true;
    }

    public void setUICallback(DeviceSettingsActivity activity) {

        this.activity = activity;
    }
}
