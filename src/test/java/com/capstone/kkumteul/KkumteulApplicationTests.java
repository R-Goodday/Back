package com.capstone.kkumteul;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"KAFKA_URL=localhost:9092",
		"FAIRYTALE_GENERATION=fairytale_generate",
		"VOCAB_EXTRACTED=vocab_extracted"
})
class KkumteulApplicationTests {

	@Test
	void contextLoads() {
	}

}
