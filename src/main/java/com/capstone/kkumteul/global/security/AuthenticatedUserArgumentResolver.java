package com.capstone.kkumteul.global.security;

import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.exception.UserNotFoundException;
import com.capstone.kkumteul.global.exception.BaseException;
import com.capstone.kkumteul.global.response.code.ErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link AuthUser} 어노테이션이 선언된 컨트롤러 메서드 파라미터에
 * 현재 인증된 사용자(User) 객체를 자동으로 주입하는 Argument Resolver.
 *
 * <p>Spring MVC의 {@link HandlerMethodArgumentResolver}를 구현하여,
 * SecurityContext에 저장된 인증 정보로부터 User 엔티티를 추출한다.</p>
 *
 * <p>동작 흐름:</p>
 * <ol>
 *     <li>컨트롤러 파라미터에 @AuthUser가 있고 타입이 User인지 확인</li>
 *     <li>SecurityContextHolder에서 Authentication 객체를 가져옴</li>
 *     <li>인증 정보가 없으면 BAD_REQUEST 예외 발생</li>
 *     <li>Principal이 CustomUserDetails이면 User 객체를 반환</li>
 *     <li>그 외의 경우 UserNotFoundException 발생</li>
 * </ol>
 *
 * @see AuthUser
 * @see CustomUserDetails
 */
@Component
@RequiredArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 이 Resolver가 해당 파라미터를 처리할 수 있는지 판별한다.
     *
     * <p>다음 두 가지 경우 중 하나에 해당하면 true를 반환한다:</p>
     * <ul>
     *     <li>파라미터에 @AuthUser가 선언되어 있고, 타입이 User인 경우</li>
     *     <li>메서드에 @AuthUser가 선언되어 있고, 파라미터 타입이 User인 경우</li>
     * </ul>
     *
     * @param parameter 컨트롤러 메서드의 파라미터 정보
     * @return @AuthUser 어노테이션이 파라미터 또는 메서드에 있고 파라미터 타입이 User.class이면 true
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthUser = parameter.hasParameterAnnotation(AuthUser.class)
                || parameter.hasMethodAnnotation(AuthUser.class);
        return hasAuthUser && parameter.getParameterType().equals(User.class);
    }

    /**
     * SecurityContext에서 현재 인증된 사용자의 User 객체를 추출하여 반환한다.
     *
     * @param parameter    컨트롤러 메서드의 파라미터 정보
     * @param mavContainer ModelAndView 컨테이너
     * @param webRequest   현재 웹 요청
     * @param binderFactory 데이터 바인더 팩토리
     * @return 현재 인증된 사용자의 User 엔티티
     * @throws BaseException 인증 정보가 없거나 인증되지 않은 경우
     * @throws UserNotFoundException Principal이 CustomUserDetails가 아닌 경우
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // SecurityContextHolder에서 Authentication 객체 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 인증되지 않은 경우 예외 발생
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(ErrorResponseCode.BAD_REQUEST_ERROR);
        }

        // Principal 객체 추출
        Object principal = authentication.getPrincipal();

        // CustomUserDetails 타입이면 내부의 User 엔티티를 반환
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        // 예상하지 못한 Principal 타입인 경우 사용자를 찾을 수 없음 예외 발생
        throw new UserNotFoundException();
    }
}
