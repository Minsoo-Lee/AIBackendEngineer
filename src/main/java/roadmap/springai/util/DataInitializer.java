package roadmap.springai.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import roadmap.springai.service.DocumentIngestionService;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final DocumentIngestionService documentIngestionService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 기존 데이터 전체 삭제
        jdbcTemplate.execute("DELETE FROM vector_store");

        documentIngestionService.ingest();
    }
}
