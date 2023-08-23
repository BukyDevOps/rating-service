package buky.example.ratingservice.controller;

import buky.example.ratingservice.clients.UserClient;
import buky.example.ratingservice.model.Rating;
import buky.example.ratingservice.model.User;
import buky.example.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQlController {

    private final RatingService ratingService;
    private final UserClient userClient;

    @QueryMapping
    List<Rating> getAllHostRatings(@Argument Long id) {
        return ratingService.getAllHostRatings(id).getRatingList();
    }

    @QueryMapping
    List<Rating> getAllAccommodationRatings(@Argument Long id) {
        return ratingService.getAllAccommodationRatings(id).getRatingList();
    }

    @SchemaMapping(typeName="Rating", field="guest")
    public User getAuthor(Rating rating) {
        return userClient.getUserById(rating.getGuestId());
    }

    @SchemaMapping(typeName="Rating", field="createdAt")
    public String getCreatedAt(Rating rating) {
        return rating.getCreatedAt().toString();
    }
}
