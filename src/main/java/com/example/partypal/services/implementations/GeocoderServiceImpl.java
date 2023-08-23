package com.example.partypal.services.implementations;

import com.example.partypal.custom_exceptions.ApiException;
import com.example.partypal.models.dtos.ApiResponse;
import com.example.partypal.services.GeocoderService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeocoderServiceImpl implements GeocoderService {

    @Value("${geocoder.url}")
    private String url;

    @Value("${geocoder.key}")
    private String key;

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Override
    public String getPlace(Double longitude, Double latitude) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ApiResponse apiResponse = executeRequest(url + "/?apikey=" + key + "&geocode="+ longitude + "," + latitude+ "&format=json", HttpMethod.GET, requestEntity);
        JsonObject response = apiResponse.getJson().getAsJsonObject("response")
                .getAsJsonObject("GeoObjectCollection")
                .getAsJsonArray("featureMember").get(0).getAsJsonObject()
                .getAsJsonObject("GeoObject");
        return response.get("name").getAsString();
    }

    public ApiResponse executeRequest(String url, HttpMethod method, HttpEntity<?> requestEntity) {
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, method, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            int statusCode = responseEntity.getStatusCode().value();
            JsonObject responseJson;
            JsonObject responseHeaders = new JsonObject();
            if (responseBody != null) {
                responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            } else {
                return new ApiResponse(responseHeaders, statusCode);
            }
            HttpHeaders headers = responseEntity.getHeaders();
            headers.forEach((key, value) -> responseHeaders.addProperty(key, value.get(0)));
            return new ApiResponse(responseJson, responseHeaders, statusCode);
        } catch (RestClientException e) {
            throw new ApiException(e.getMessage());
        }
    }
}
