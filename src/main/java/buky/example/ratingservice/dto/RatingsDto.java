package buky.example.ratingservice.dto;

import buky.example.ratingservice.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingsDto {

    private List<Rating> ratingList;
    private Double averageRating;

}
