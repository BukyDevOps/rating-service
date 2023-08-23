package buky.example.ratingservice.clients;

import buky.example.ratingservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value(value = "${user.BaseURL}")
    private String baseURL;

    public User getUserById(Long id) {
        String endpoint = baseURL + "/" + id;
        return sendRequest(endpoint);
    }

    private User sendRequest(String endpoint) {
        try {
            ResponseEntity<User> responseEntity = restTemplate.exchange(endpoint, HttpMethod.GET,
                    new HttpEntity<User>(new HttpHeaders()),
                    ParameterizedTypeReference.forType(User.class));

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                if(responseEntity.getBody() == null) return null;

                return responseEntity.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
