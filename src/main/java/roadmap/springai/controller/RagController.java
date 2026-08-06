package roadmap.springai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import roadmap.springai.service.RagService;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    @GetMapping("/v1")
    public String ragV1(@RequestParam String question) {
        return ragService.answerV1(question);
    }

    @GetMapping("/v2")
    public String ragV2(@RequestParam String question) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String result = ragService.answerV2(question);

        stopWatch.stop();
        log.info("⏱️ 응답 시간: {}ms", stopWatch.getTotalTimeMillis());

        return result;
    }

    @GetMapping("/v3")
    public String ragV3(@RequestParam String question, Principal principal) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String result = ragService.answerV3(question, principal.getName());

        stopWatch.stop();
        log.info("⏱️ 응답 시간: {}ms", stopWatch.getTotalTimeMillis());

        return result;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String question) {
        return ragService.stream(question);
    }

    @GetMapping("/safe/v1")
    public String safeAnswerV1(@RequestParam String question) {
        return ragService.safeAnswerV1(question);
    }

    @GetMapping("/safe/v2")
    public ResponseEntity<String> safeAnswerV2(@RequestParam String question) {
        try {
            return ResponseEntity.ok(ragService.safeAnswerV2(question));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/safe/v3")
    public String safeAnswerV3(@RequestParam String question) {
        return ragService.safeAnswerV3(question);
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String question,
                       @RequestParam String conversationId) {
        return ragService.chat(question, conversationId);
    }
}
