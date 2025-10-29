package com.navershop.navershop.todo.repository.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true, length = 100)
    private String address;

    @Column(name = "provider_id", length = 255)
    private String providerId; // OAuth Provider의 사용자 ID

    @Column(name = "profile_img_url", length = 255)
    private String profileImageUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

}