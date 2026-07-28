package roadmap.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 질문을 받아 벡터로 변환 후 pgvector 에서 유사한 문서를 찾아 상위 3개를 반환하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSearchService {

    private final VectorStore vectorStore;

    public List<Document> search(String query) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .similarityThreshold(0.6)
                        .build()
        );
        log.info("🔍 검색 결과: {}개", results.size());
        return results;
    }
}
