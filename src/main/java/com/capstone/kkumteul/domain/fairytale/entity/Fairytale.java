package com.capstone.kkumteul.domain.fairytale.entity;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Fairytale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fairytale_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Morality morality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharSpecies charSpecies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Background background;

    @Builder.Default
    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paragraph> paragraphs = new ArrayList<>();

}
