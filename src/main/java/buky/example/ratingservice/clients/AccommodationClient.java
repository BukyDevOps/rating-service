package buky.example.ratingservice.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AccommodationClient {

    private final RestTemplate restTemplate;

    @Value(value = "${accommodation.BaseURL}")
    private String baseURL;

    public Long getAccommodationHostById(Long accommodationId) {
        String endpoint = baseURL + "/host/" + accommodationId;

        try {
            ResponseEntity<Long> responseEntity = restTemplate.exchange(endpoint, HttpMethod.GET,
                    new HttpEntity<Long>(new HttpHeaders()),
                    Long.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
