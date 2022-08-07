package com.neu.client.restClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestClient {

    /**
     * The url mapping of the rest api
     */
//    @Value("${request.uri}")
    private String uri = "http://localhost:9000/user";

    private final RestTemplate restTemplate;

    public RestClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Use for user signup.
     *
     * @param nickname the user nickname
     * @param email the user email should be checked by regex before calling the method
     * @param password the user password
     * @return response body of the http request if the code is 200
     * @throws HttpClientErrorException thrown when the code is 4xx, usually caused by the email has been registered,
     * use getResponseBodyAsString() to get the error message from the server.
     * @throws HttpServerErrorException thrown when the code is 5xx, problem with server
     * @throws ResourceAccessException thrown when the server is not reachable, server crash, not start, etc.
     */
    public String signup(@NotNull String nickname, @NotNull String email, @NotNull String password) throws HttpClientErrorException, HttpServerErrorException, ResourceAccessException {
        // request address
        String url =  uri + "/signup";
        System.out.println(url);
        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // request body
        HashMap<String, String> body = new HashMap<>();
        body.put("nickname", nickname);
        body.put("email", email);
        body.put("password", password);
        // construct the request
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        // send the request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        // response body
        return responseEntity.getBody();
    }

    /**
     * Use for user login.
     *
     * @param email the user email should be checked by regex before calling the method
     * @param password the password of the user
     * @param hostname the hostname -> get from localhost InetAddress.getLocalHost().getHostName()
     * @param port the port of the client start -> get from command line args when the Client application started
     * @return a map contains the id of the user and the hostname, port of the leader node of the p2p network
     * map key values {"id": xxx, "hostname": "xxx", "port": xxx}
     * @throws HttpClientErrorException thrown when the code is 4xx, usually caused by the email doesn't exist or password incorrect,
     * use getResponseBodyAsString() to get the error message from the server.
     * @throws HttpServerErrorException thrown when the code is 5xx, problem with server
     * @throws ResourceAccessException thrown when the server is not reachable, server crash, not start, etc.
     */
    public Map<String, Object> login(String email, String password, @NotNull String hostname, @NotNull int port) throws HttpClientErrorException, HttpServerErrorException, ResourceAccessException {
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
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity;
        Map<String, Object> result = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            // send the request
            responseEntity = restTemplate.postForEntity(url, request, String.class);
            // response
            result = mapper.readValue(responseEntity.getBody(), Map.class);
        } catch (HttpClientErrorException hcee) {
            try {
                result = mapper.readValue(hcee.getResponseBodyAsString(), Map.class);
            } catch (JsonProcessingException ignored) {}
            assert result != null;
            throw new HttpClientErrorException(hcee.getStatusCode(), "", result.get("error").toString().getBytes(), StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {}
        return result;
    }

//    public static void main(String[] args) {
//        RestClient restClient = new RestClient();
//        try {
////            String res = restClient.signup("fancle", "12345@gmail.com", "123456");
//            Map<String, Object> res = restClient.login("12@gmail.com", "123456", InetAddress.getLocalHost().getHostName(), 9003);
//            System.out.println(res.get("port"));
//        } catch (HttpClientErrorException e) {
//            // http code other than 200
//            System.out.println(e.getResponseBodyAsString());
//        } catch (ResourceAccessException e) {
//            System.out.println(e.getMessage());;
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//    }

}