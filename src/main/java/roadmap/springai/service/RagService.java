package roadmap.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    public String answer(String question) {
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
