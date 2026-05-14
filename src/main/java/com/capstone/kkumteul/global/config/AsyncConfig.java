package com.capstone.kkumteul.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 작업 전용 Executor 빈 등록.
 *
 * <p>현재는 그래프 추출 (FastAPI → OpenAI) 호출 1 종류에만 사용. 풀 사이즈는 캡스톤 시연 부하 기준으로 산정하며
 * 동화 동시 생성 부하가 늘어나면 core/max 를 상향 조정한다.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "graphExtractExecutor")
    public Executor graphExtractExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("graph-extract-");
        executor.initialize();
        return executor;
    }
}
