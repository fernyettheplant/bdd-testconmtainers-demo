package dev.fern.integrationtestdemo.ui.http.ping;

import dev.fern.integrationtestdemo.ui.http.ping.response.PingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ping")
public class PingController {
    @GetMapping
    public ResponseEntity<PingResponse> get() {
        return new ResponseEntity<>(new PingResponse("Ping responseto you dev"), HttpStatus.OK);
    }
}
