package com.example.chapter04.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

// JobExecutionListener 를 구현하지 않고 @BeforeJob @AfterJob 애너테이션을 사용하여 리스너를 등록할 수도 있다.
// 이때 JobListenerFactoryBean.getListener()를 사용한다.
public class JobLoggerListener implements JobExecutionListener {

    private static String START_MESSAGE = "%s is beginning execution";
    private static String END_MESSAGE = "%s has completed with the status %s";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println(String.format(
                (START_MESSAGE),
                jobExecution.getJobInstance().getJobName()
        ));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // afterJob 메서드는 잡의 실행 종료 상태와 무관하게 항상 실행된다.
        System.out.println(String.format(
                END_MESSAGE,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()
        ));

    }
}
