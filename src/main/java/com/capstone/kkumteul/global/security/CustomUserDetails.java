package com.capstone.kkumteul.global.security;

import com.capstone.kkumteul.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 {@link UserDetails}를 구현한 커스텀 사용자 인증 정보 클래스.
 *
 * <p>애플리케이션의 {@link User} 엔티티를 감싸서(Wrap) Spring Security가
 * 인증 및 인가 처리에 필요한 사용자 정보를 제공한다.</p>
 *
 * <p>SecurityContext에 저장되어 인증된 사용자의 정보를 어디서든 조회할 수 있도록 하며,
 * {@link AuthenticatedUserArgumentResolver}를 통해 컨트롤러에서 User 객체로 변환된다.</p>
 *
 * @see AuthenticatedUserArgumentResolver
 * @see User
 */
@Getter
public class CustomUserDetails implements UserDetails {

    /** 인증된 사용자의 엔티티 객체 */
    private final User user;

    /**
     * CustomUserDetails 생성자.
     *
     * @param user 인증할 사용자 엔티티
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자에게 부여된 권한(Role) 목록을 반환한다.
     *
     * @return 사용자의 역할(Role)을 기반으로 생성된 GrantedAuthority 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    /**
     * 사용자의 암호화된 비밀번호를 반환한다.
     *
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 로그인 아이디(username)를 반환한다.
     *
     * @return 사용자명
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 계정 만료 여부를 반환한다.
     *
     * @return 만료되지 않았으면 true (기본값 사용)
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * 계정 잠금 여부를 반환한다.
     *
     * @return 잠금되지 않았으면 true (기본값 사용)
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * 자격 증명(비밀번호) 만료 여부를 반환한다.
     *
     * @return 만료되지 않았으면 true (기본값 사용)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * 계정 활성화 여부를 반환한다.
     *
     * @return 활성화 상태이면 true (기본값 사용)
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
