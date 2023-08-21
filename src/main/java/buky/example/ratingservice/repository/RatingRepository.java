package buky.example.ratingservice.repository;

import buky.example.ratingservice.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("select r from Rating r where r.active = true and r.hostRating = true and r.subjectId = ?1")
    List<Rating> findAllHostRatings(Long subjectId);

    @Query("select r from Rating r where r.active = true and r.hostRating = false and r.subjectId = ?1")
    List<Rating> findAllAccommodationRatings(Long accommodationId);

    Optional<Rating> findRatingByHostRatingTrueAndGuestIdAndSubjectId(Long userId, Long hostId);

    Optional<Rating> findRatingByHostRatingFalseAndGuestIdAndSubjectId(Long userId, Long accommodationId);
}
