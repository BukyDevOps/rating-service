package buky.example.ratingservice.controller;

import buky.example.ratingservice.dto.RatingsDto;
import buky.example.ratingservice.model.Rating;
import buky.example.ratingservice.security.HasRole;
import buky.example.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable Long id){
        return ratingService.getById(id);
    }

    @GetMapping("")
    @HasRole("GUEST")
    public Rating getRatingByHostId(@RequestParam Long hostId, Long userId){
        return ratingService.getRatingForHost(hostId, userId);
    }

    @GetMapping("")
    @HasRole("GUEST")
    public Rating getRatingByAccommodationId(@RequestParam Long accommodationId, Long userId){
        return ratingService.getRatingForAccommodation(accommodationId, userId);
    }

    @PostMapping
    @HasRole("GUEST")
    public Rating addRating(@RequestBody Rating newRating, Long userId) {
        return ratingService.addRating(newRating, userId);
    }

    @PutMapping("/{id}")
    @HasRole("GUEST")
    public Rating updateRating(@PathVariable Long id, @RequestBody Rating updatedRating, Long userId) {
        return ratingService.updateRating(updatedRating, userId, id);
    }

    @DeleteMapping ("/{id}")
    @HasRole("GUEST")
    public void deleteRating(@PathVariable Long id, Long userId) {
        ratingService.deleteRating(userId, id);
    }

    @GetMapping("/host/{id}")
    public RatingsDto getAllRatingsForHost(@PathVariable Long id) {
        return ratingService.getAllHostRatings(id);
    }

    @GetMapping("/accommodation/{id}")
    public RatingsDto getAllRatingsForAccommodation(@PathVariable Long id) {
        return ratingService.getAllAccommodationRatings(id);
    }

}
