package org.example.server.notification.infrastructure.batch;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.example.server.notification.application.DailyNotificationBatchService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DailyNotificationBatchConfig {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    @Bean
    public Job dailyNotificationJob(JobRepository jobRepository, Step dailyNotificationStep) {
        return new JobBuilder("dailyNotificationJob", jobRepository).start(dailyNotificationStep).build();
    }

    @Bean
    public Step dailyNotificationStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DailyNotificationBatchService batchService
    ) {
        return new StepBuilder("dailyNotificationStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                batchService.sendDueNotifications(LocalDateTime.now(SEOUL_ZONE));
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

}
