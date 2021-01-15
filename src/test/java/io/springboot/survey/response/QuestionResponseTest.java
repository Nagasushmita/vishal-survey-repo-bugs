package io.springboot.survey.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.meanbean.test.Configuration;
import org.meanbean.test.ConfigurationBuilder;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@Tag("Response")
class QuestionResponseTest {

    @Test
    @DisplayName("Question Response Test")
    void questionResponseTest() {
        BeanTester beanTester=new BeanTester();
        Configuration configuration=new ConfigurationBuilder()
                .ignoreProperty("fileData")
                .build();
        beanTester.testBean(QuestionResponse.class,configuration);
        byte[] bytes= new byte[23];
        QuestionResponse questionResponse=new QuestionResponse();
        questionResponse.setFileData(bytes);
        assertEquals(bytes,questionResponse.getFileData());
    }

}