package com.capstone.kkumteul.global.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증된 사용자(User)를 컨트롤러 메서드 파라미터에 주입하기 위한 커스텀 어노테이션.
 *
 * <p>컨트롤러 메서드의 파라미터에 이 어노테이션을 선언하면,
 * {@link AuthenticatedUserArgumentResolver}가 SecurityContext에서
 * 현재 인증된 사용자(User) 객체를 자동으로 바인딩해 준다.</p>
 *
 * <p>메서드에 선언할 경우, 해당 메서드가 인증된 사용자를 필요로 함을 명시적으로 나타낸다.</p>
 *
 * <p>사용 예시:</p>
 * <pre>
 *     // 파라미터에 사용
 *     @GetMapping("/profile")
 *     public ResponseEntity getProfile(@AuthUser User user) {
 *         // user: 현재 로그인한 사용자 객체
 *     }
 *
 *     // 메서드에 사용
 *     @AuthUser
 *     @GetMapping("/my-page")
 *     public ResponseEntity getMyPage() { ... }
 * </pre>
 *
 * @see AuthenticatedUserArgumentResolver
 */
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 어노테이션 정보를 유지하여 리플렉션으로 조회 가능
@Target({ElementType.PARAMETER, ElementType.METHOD})  // 메서드 파라미터 및 메서드에 사용 가능
public @interface AuthUser {
}
