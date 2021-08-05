package com.keti.weather.collector.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


public abstract class AbstractDynamicScheduled {
    
    private int poolSize;
    private String threadNamePrefix;

    private ThreadPoolTaskScheduler scheduler;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public AbstractDynamicScheduled() {
        this(1, "weather-collector-");
    }


    public AbstractDynamicScheduled(int poolSize, String threadNamePrefix) {
        this.poolSize = poolSize;
        this.threadNamePrefix = threadNamePrefix;
    }


    public void stopScheduler() {
        if(scheduler != null) {
            scheduler.shutdown();
        }
    }

    
    public void startScheduler() {
		logger.info("##### Scheduler Configuration");
        logger.info("##### poolSize: " + poolSize);
        logger.info("##### threadNamePrefix: " + threadNamePrefix);

        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        scheduler.schedule(getRunnable(), getTrigger());
    }


    private Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                runner();
            }
        };
    }


    public abstract void runner();
    public abstract Trigger getTrigger();
    
}
