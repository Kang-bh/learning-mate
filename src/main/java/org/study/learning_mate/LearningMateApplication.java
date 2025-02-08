package org.study.learning_mate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LearningMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningMateApplication.class, args);
	}

}
