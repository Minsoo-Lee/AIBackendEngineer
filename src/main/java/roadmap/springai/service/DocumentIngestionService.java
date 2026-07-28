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

        // 3. VectorStore에 저장 (임베딩 변환 + 저장 자동으로 해 줌)
        vectorStore.add(split1);
        vectorStore.add(split2);

        log.info("✅ 문서 임베딩 저장 완료!");
    }
}
