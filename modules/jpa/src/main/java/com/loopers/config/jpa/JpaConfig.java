package com.loopers.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // 트랜잭션 관리 기능을 활성화
@EntityScan({"com.loopers"}) // 엔티티 스캔 범위 지정
@EnableJpaRepositories({"com.loopers.infrastructure"}) // 레포지토리 스캔 범위 지정
public class JpaConfig {
}
