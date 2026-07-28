package roadmap.springai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roadmap.springai.service.DocumentSearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final DocumentSearchService documentSearchService;

    @GetMapping("/search")
    public List<Document> search(@RequestParam String query) {
        return documentSearchService.search(query);
    }
}
