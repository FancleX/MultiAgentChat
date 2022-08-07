package com.neu.client.restClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestClient {

    /**
     * The url mapping of the rest api
     */
    @Value("${request.uri}")
    private String uri;

    @Resource
    private RestTemplate restTemplate;

    public RestClient() {}

    public Map<String, Object> signup(String nickname, String email, String password) {
        // request address
        String url =  uri + "/signup";
        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // request body
        HashMap<String, String> body = new HashMap<>();
        body.put("nickname", nickname);
        body.put("email", email);
        body.put("password", password);
        // construct the request
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        // send the request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        // response
        HttpStatus statusCode = responseEntity.getStatusCode();
        String entityBody = responseEntity.getBody();
        HashMap<String, Object> res = new HashMap<>();
        res.put("statusCode", statusCode);
        res.put("body", entityBody);
        return res;
    }

    public Map<String, Object> login(String email, String password, String hostname, int port) {
        // request address
        String url = uri + "/login";
        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // request body
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("hostname", hostname);
        body.put("port", port);
        // construct the request
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        // send the request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        // response
        HashMap<String, Object> res = new HashMap<>();
        HttpStatus statusCode = responseEntity.getStatusCode();
        String entityBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> readValue;
        try {
            readValue = objectMapper.readValue(entityBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        res.put("statusCode", statusCode);
        res.put("body", readValue);
        return res;
    }

}
