package plugins.philipsHue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpClient {

	Logger logger = LoggerFactory.getLogger(HttpClient.class);
	
    public String sendHttpRequest(String urlAsString, String body, String method) throws Exception {
        String responseBody = null; //TODO
        try {
            logger.info(urlAsString);
            logger.info(body);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            RestTemplate restTemplate = new RestTemplate();
            // Data attached to the request.
            HttpEntity<String> requestBody = new HttpEntity<>(body, headers);
            // Send request with PUT method.
            restTemplate.exchange(urlAsString, HttpMethod.PUT, requestBody, String.class);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception(exception.getMessage());
        }
        return responseBody;
    }
}
