package com.expensemini.backend.controller;

import com.expensemini.backend.dto.CurrencyConversionResponse;
import com.expensemini.backend.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(
            @RequestParam(defaultValue = "USD") String from,
            @RequestParam(defaultValue = "EUR") String to,
            @RequestParam(defaultValue = "1.0") BigDecimal amount) {
        return ResponseEntity.ok(externalApiService.getExchangeRates(from, to, amount));
    }
}
