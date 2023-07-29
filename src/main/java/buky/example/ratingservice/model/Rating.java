package buky.example.ratingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long guestId;
    private Boolean hostRating;
    private Long subjectId;
    private Byte ratingValue;
    private LocalDateTime createdAt;
    private String description;
    private Boolean active;
}
