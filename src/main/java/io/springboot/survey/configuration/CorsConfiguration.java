package io.springboot.survey.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static io.springboot.survey.utils.Constants.CorsConfigurationConstant.*;

@Configuration
public class CorsConfiguration implements Filter,WebMvcConfigurer  {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(CORS_PATTERN);
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader(ALLOW_ORIGIN, ASTERISK);
        response.setHeader(ALLOW_METHOD, ALL_MAPPING);
        response.setHeader(ALLOW_HEADER, ALL_HEADERS);
        response.setHeader(MAX_AGE, MAX_AGE_3600);
        response.setHeader(ALLOW_CREDENTIALS, BOOLEAN_TRUE);
        response.setHeader(EXPOSE_HEADER, HEADER_AUTHORIZATION);
        response.addHeader(EXPOSE_HEADER, HEADER_RESPONSE_TYPE);
        response.addHeader(EXPOSE_HEADER, HEADER_OBSERVE);
        if (!(request.getMethod().equalsIgnoreCase(OPTION_MAPPING))) {
                chain.doFilter(req, res);

        } else {
            response.setHeader(ALLOW_ORIGIN, ASTERISK);
            response.setHeader(ALLOW_HEADER, MAPPINGS);
            response.setHeader(MAX_AGE, MAX_AGE_3600);
            response.setHeader(ALLOW_HEADER, OPTION_HEADER);
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }

}
