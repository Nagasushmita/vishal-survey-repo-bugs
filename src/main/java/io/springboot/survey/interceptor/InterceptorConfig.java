package io.springboot.survey.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.getExcludedUri;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    final Interceptor interceptor;

    public InterceptorConfig(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).excludePathPatterns(getExcludedUri())
        .excludePathPatterns( "/webjars/**","/swagger-resources/**").pathMatcher(new AntPathMatcher());
    }
}
