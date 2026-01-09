package com.dev.core.ecommerce.common.auth;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.dev.core.ecommerce.common.error.ErrorType.INVALID_REQUEST;

/**
 * 해당 서버는 API Gateway 뒷 단에 private 존에 있는 서버라고 가정
 * 사용자 인증은 API Gateway 에서 처리 후 헤더를 통해 userId 값을 전달해주는 상태로 가정
 */
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public User resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull(request, INVALID_REQUEST.getMessage());
        String userId = request.getHeader("Ecommerce-User-Id");
        return new User(Long.parseLong(userId));
    }
}
