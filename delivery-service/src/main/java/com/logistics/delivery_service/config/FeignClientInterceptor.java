//package com.logistics.delivery_service.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import jakarta.servlet.http.HttpServletRequest;
//
//@Component
//public class FeignClientInterceptor implements RequestInterceptor {
//
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String BEARER_TOKEN_TYPE = "Bearer";
//
//    @Override
//    public void apply(RequestTemplate template) {
//        // Récupérer le token JWT de la requête actuelle
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        if (attributes != null) {
//            HttpServletRequest request = attributes.getRequest();
//            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
//
//            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_TOKEN_TYPE)) {
//                // Propager le token aux appels Feign
//                template.header(AUTHORIZATION_HEADER, authorizationHeader);
//            }
//        }
//    }
//}