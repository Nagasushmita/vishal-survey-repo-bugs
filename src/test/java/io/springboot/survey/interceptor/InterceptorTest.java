package io.springboot.survey.interceptor;

import io.springboot.survey.exception.AuthorizationException;
import io.springboot.survey.exception.ResourceNotFoundException;
import io.springboot.survey.utils.AuthorizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static io.springboot.survey.utils.Constants.ErrorMessageConstant.USER_UNAUTHORIZED;
import static io.springboot.survey.utils.Constants.NullEmptyConstant.JWT_NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Interceptor")
class InterceptorTest {

    @InjectMocks
    Interceptor interceptor;

    @Mock
    AuthorizationService authorizationService;

    @Nested
    @DisplayName("InterceptorTest")
    class PreHandleTest {
        @Test
        @DisplayName("Invalid URI")
        void preHandle() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("GET","/urlpath/soemname");
            MockHttpServletResponse response = new MockHttpServletResponse();
            Object object=Mockito.mock(Object.class);
            boolean result=interceptor.preHandle(request,response,object);
            assertFalse(result);
        }
        @Test
        @DisplayName("Empty Token")
        void emptyToken() {
            MockHttpServletRequest request = new MockHttpServletRequest("GET","/urlpath/soemname");
            MockHttpServletResponse response = new MockHttpServletResponse();
            Object object=Mockito.mock(Object.class);
            when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
            Exception exception=assertThrows(ResourceNotFoundException.class,()->interceptor.preHandle(request,response,object));
            assertEquals(JWT_NULL,exception.getMessage());
        }

        @Test
        @DisplayName("User Not Authorized")
        void notAuthorized() {
            MockHttpServletRequest request = new MockHttpServletRequest("GET","/urlpath/soemname");
            MockHttpServletResponse response = new MockHttpServletResponse();
            Object object=Mockito.mock(Object.class);
            request.addHeader("Authorization","mockedToken");
            when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
            when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(false);
            Exception exception=assertThrows(AuthorizationException.class,()->interceptor.preHandle(request,response,object));
            assertEquals(USER_UNAUTHORIZED,exception.getMessage());
        }

        @Test
        @DisplayName("UserAuthorized")
        void success() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("GET","/urlpath/soemname");
            MockHttpServletResponse response = new MockHttpServletResponse();
            Object object=Mockito.mock(Object.class);
            request.addHeader("Authorization","mockedToken");
            when(authorizationService.showEndpointsAction(Mockito.anyString())).thenReturn(true);
            when(authorizationService.authorizationManager(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
            boolean result=interceptor.preHandle(request,response,object);
            assertTrue(result);
        }
    }
}