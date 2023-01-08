package com.example.chapter04.config;

import com.example.chapter04.incrementer.DailyJobTimestamper;
import com.example.chapter04.listener.JobLoggerListener;
import com.example.chapter04.validator.ParameterValidator;
import java.util.Arrays;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
                new String[]{"fileName"},
                new String[]{"name", "currentDate"}
        );

        defaultJobParametersValidator.afterPropertiesSet();

        validator.setValidators(Arrays.asList(
                new ParameterValidator(),
                defaultJobParametersValidator
        ));

        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
                .incrementer(new DailyJobTimestamper())
                .listener(new JobLoggerListener())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null, null))
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileName']}") String fileName) {

        return (contribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s!", name));
            System.out.println(String.format("fileName = %s", fileName));
            return RepeatStatus.FINISHED;
        };
    }

}
