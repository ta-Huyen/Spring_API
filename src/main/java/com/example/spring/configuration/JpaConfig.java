package com.example.spring.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.example.spring.repository")
@EntityScan(basePackageClasses = {Jsr310JpaConverters.class}, basePackages = {"com.example.spring.entity"})
public class JpaConfig {}