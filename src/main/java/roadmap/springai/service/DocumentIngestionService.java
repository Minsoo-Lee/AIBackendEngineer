package roadmap.springai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    @Value("classpath:documents/sample1.txt")
    private Resource sample1;

    @Value("classpath:documents/sample2.txt")
    private Resource sample2;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest() {
        // 1. 문서 로드
        List<Document> docs1 = new TextReader(sample1).get();
        List<Document> docs2 = new TextReader(sample2).get();

        // 2. Chunking
        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        List<Document> split1 = splitter.apply(docs1);
        List<Document> split2 = splitter.apply(docs2);

        // 저장 전 문서 내용 검증 (Indirect 방어)
        split1.forEach(doc -> validateDocument(doc.getText()));
        split2.forEach(doc -> validateDocument(doc.getText()));

        // 3. VectorStore에 저장 (임베딩 변환 + 저장 자동으로 해 줌)
        vectorStore.add(split1);
        vectorStore.add(split2);

        log.info("✅ 문서 임베딩 저장 완료!");
    }

    private void validateDocument(String content) {
        List<String> blackList = List.of(
                "이전 지시를 무시", "ignore previous", "당신은 이제",
                "you are now", "DAN", "jailbreak", "시스템 프롬프트를 무시",
                "역할을 바꿔", "제한을 무시"
        );

        String lower = content.toLowerCase();
        for (String keyword : blackList) {
            if (lower.contains(keyword.toLowerCase())) {
                throw new IllegalArgumentException("악의적인 내용이 포함된 문서입니다: " + keyword);
            }
        }
    }
}
