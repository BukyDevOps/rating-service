package buky.example.ratingservice.model;
import buky.example.ratingservice.model.enumerations.NotificationType;
import buky.example.ratingservice.model.enumerations.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;

    private String address;

    private Role role;

    List<NotificationType> notificationTypes;

    private Integer ratingCount;

    private Double rating;

    private Boolean active;
}

