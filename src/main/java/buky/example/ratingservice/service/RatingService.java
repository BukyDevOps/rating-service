package buky.example.ratingservice.service;

import buky.example.ratingservice.dto.RatingsDto;
import buky.example.ratingservice.exceptions.NotFoundException;
import buky.example.ratingservice.exceptions.RatingNotPossibleException;
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

        return ratingRepository.save(newRating);
    }

    private boolean userHasPreviousReservations(Long userId, Long subjectId) {
        //TODO Kafka za provjeru rezervacija
        return true;
    }

    private boolean userStayedIn(Long userId, Long subjectId) {
        //TODO Kafka za provjeru rezervacija
        return true;
    }

    public Rating updateRating(Rating updatedRating, Long userId, Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating is not found!"));

        if(!rating.getActive() || !rating.getGuestId().equals(userId))
            throw new NotFoundException("Rating is not found!");

        updatedRating.setCreatedAt(LocalDateTime.now());
        updatedRating.setActive(true);

        return ratingRepository.save(updatedRating);
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
}
