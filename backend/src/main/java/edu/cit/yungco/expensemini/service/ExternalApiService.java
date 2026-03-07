package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.CurrencyConversionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final String FRANKFURTER_API = "https://api.frankfurter.app/latest";

    public CurrencyConversionResponse getExchangeRates(String fromCurrency, String toCurrency, BigDecimal amount) {
        try {
            String url = String.format("%s?amount=%f&from=%s&to=%s",
                    FRANKFURTER_API, amount, fromCurrency, toCurrency);

            return restTemplate.getForObject(url, CurrencyConversionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch exchange rates. " + e.getMessage());
        }
    }
}
