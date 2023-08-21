package buky.example.ratingservice.service;

import buky.example.ratingservice.clients.AccommodationClient;
import buky.example.ratingservice.clients.ReservationClient;
import buky.example.ratingservice.dto.RatingsDto;
import buky.example.ratingservice.exceptions.NotFoundException;
import buky.example.ratingservice.exceptions.RatingNotPossibleException;
import buky.example.ratingservice.messaging.KafkaProducer;
import buky.example.ratingservice.messaging.messages.AccommodationRatingMessage;
import buky.example.ratingservice.messaging.messages.HostRatingMessage;
import buky.example.ratingservice.model.Rating;
import buky.example.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final KafkaProducer kafkaProducer;
    private final AccommodationClient accommodationClient;
    private final ReservationClient reservationClient;

    public Rating getById(Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));
        if(!rating.getActive()) throw new NotFoundException("Rating is deleted!");

        return rating;
    }

    public Rating addRating(Rating newRating, Long userId) {

        if(newRating.getHostRating() && !userHasPreviousReservations(userId, newRating.getSubjectId()))
            throw new RatingNotPossibleException("You didn't have any reservation at this host!");

        if(!newRating.getHostRating() && !userStayedIn(userId, newRating.getSubjectId()))
            throw new RatingNotPossibleException("You have not stayed in this accommodation ever!");

        newRating.setGuestId(userId);
        newRating.setCreatedAt(LocalDateTime.now());
        newRating.setActive(true);

        Rating rating = ratingRepository.save(newRating);

        if(rating.getHostRating()) {
            kafkaProducer.send("host-rating", new HostRatingMessage(userId,
                    rating.getSubjectId(), rating.getId(), rating.getRatingValue(),(byte)0));
        } else {
            Long hostId = accommodationClient.getAccommodationHostById(newRating.getSubjectId());
            if(hostId != null){
                kafkaProducer.send("accommodation-rating", new AccommodationRatingMessage(userId, hostId,
                        rating.getSubjectId(), rating.getId(), rating.getRatingValue()));
            }
        }

        return rating;
    }

    private boolean userHasPreviousReservations(Long userId, Long subjectId) {
        return reservationClient.userHasPreviousReservations(userId, subjectId);
    }

    private boolean userStayedIn(Long userId, Long subjectId) {
        return reservationClient.userStayed(userId, subjectId);
    }

    public Rating updateRating(Rating updatedRating, Long userId, Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating is not found!"));

        byte oldRatingValue = rating.getRatingValue();

        if(!rating.getActive() || !rating.getGuestId().equals(userId))
            throw new NotFoundException("Rating is not found!");

        updatedRating.setCreatedAt(LocalDateTime.now());
        updatedRating.setActive(true);

        ratingRepository.save(updatedRating);

        if(updatedRating.getHostRating()) {
            kafkaProducer.send("host-rating", new HostRatingMessage(userId,
                    rating.getSubjectId(), rating.getId(), updatedRating.getRatingValue(), oldRatingValue));
        } else {
            Long hostId = accommodationClient.getAccommodationHostById(updatedRating.getSubjectId());
            if(hostId != null){
                kafkaProducer.send("accommodation-rating", new AccommodationRatingMessage(userId, hostId,
                        rating.getSubjectId(), rating.getId(), rating.getRatingValue()));
            }
        }
        return updatedRating;
    }

    public void deleteRating(Long userId, Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));

        if(!rating.getGuestId().equals(userId))
            throw new RatingNotPossibleException("This rating is not your!");

        rating.setActive(false);
        ratingRepository.save(rating);
    }

    public RatingsDto getAllHostRatings(Long hostId) {
        List<Rating> ratings = ratingRepository.findAllHostRatings(hostId);
        Double averageRating = ratings.stream().mapToInt(Rating::getRatingValue).average().orElse(0.0);

        return new RatingsDto(ratings, averageRating);
    }

    public RatingsDto getAllAccommodationRatings(Long accommodationId) {
        List<Rating> ratings = ratingRepository.findAllAccommodationRatings(accommodationId);
        Double averageRating = ratings.stream().mapToInt(Rating::getRatingValue).average().orElse(0.0);

        return new RatingsDto(ratings, averageRating);
    }

    public Rating getRatingForHost(Long hostId, Long userId) {
        return ratingRepository.findRatingByHostRatingTrueAndGuestIdAndSubjectId(userId, hostId).orElse(null);
    }

    public Rating getRatingForAccommodation(Long accommodationId, Long userId) {
        return ratingRepository.findRatingByHostRatingFalseAndGuestIdAndSubjectId(userId, accommodationId).orElse(null);
    }
}
