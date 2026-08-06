package roadmap.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagCacheService {

    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    @Cacheable(value = "ragCache", key = "#question")
    public String cachedAnswer(String question) {
        log.info("🤖 RAG 호출 - 캐시 미스: {}", question);
        return chatClientBuilder.build()
                .prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(
                                SearchRequest.builder().similarityThreshold(0.6).topK(3).build())
                        .build()
                )
                .user(question)
                .call()
                .content();
    }
}
