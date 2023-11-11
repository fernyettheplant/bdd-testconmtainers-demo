package dev.fern.integrationtestdemo.ui.http.names;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/names")
public class NamesController {
    @PostMapping
    public ResponseEntity<Void> post() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
