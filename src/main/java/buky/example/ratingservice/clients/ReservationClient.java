package buky.example.ratingservice.clients;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ReservationClient {

    private final RestTemplate restTemplate;

    @Value(value = "${reservation.BaseURL}")
    private String baseURL;

    public boolean userHasPreviousReservations(Long userId, Long accommodationId) {
        String endpoint = baseURL + "/previous-reservations?userId=" + userId + "&accommodationId=" + accommodationId;
        return sendRequest(endpoint);
    }

    public boolean userStayed(Long userId, Long accommodationId) {
        String endpoint = baseURL + "/stayed-in?userId=" + userId + "&accommodationId=" + accommodationId;
        return sendRequest(endpoint);
    }

    private boolean sendRequest(String endpoint) {
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(endpoint, HttpMethod.GET,
                    new HttpEntity<Boolean>(new HttpHeaders()),
                    Boolean.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                if(responseEntity.getBody() == null) return false;

                return responseEntity.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}