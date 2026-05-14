package com.capstone.kkumteul.domain.voice.entity;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceModel extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // extracted tts model name
    @Column(unique = true, nullable = true)
    private String modelName;

    @Column(nullable = false, unique = true)
    private String wavFileUrl;
}
