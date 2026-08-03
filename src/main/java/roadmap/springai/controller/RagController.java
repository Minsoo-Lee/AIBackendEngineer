package roadmap.springai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roadmap.springai.service.RagService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @GetMapping("/rag")
    public String rag(@RequestParam String question) {
        return ragService.answer(question);
    }
}
