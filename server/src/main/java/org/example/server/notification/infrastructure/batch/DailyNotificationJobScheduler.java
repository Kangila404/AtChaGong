package org.example.server.notification.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class DailyNotificationJobScheduler {
    private final JobLauncher jobLauncher;
    @Qualifier("dailyNotificationJob")
    private final Job dailyNotificationJob;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void launchDailyNotificationJob() throws Exception {
        jobLauncher.run(dailyNotificationJob, new JobParametersBuilder()
            .addLong("launchedAt", System.currentTimeMillis())
            .toJobParameters());
    }
}
