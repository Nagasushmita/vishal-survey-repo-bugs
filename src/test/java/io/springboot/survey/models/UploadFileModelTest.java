package io.springboot.survey.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.meanbean.test.Configuration;
import org.meanbean.test.ConfigurationBuilder;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Model")
@ExtendWith(MockitoExtension.class)
class UploadFileModelTest {

    @Test
    @DisplayName("Upload File Test")
    void fileModelTest() {
        BeanTester beanTester=new BeanTester();
        Configuration configuration=new ConfigurationBuilder()
                .ignoreProperty("fileData")
                .build();
        beanTester.testBean(UploadFileModel.class,configuration);
    }

    @Test
    @DisplayName("Set File Data")
    void setFileData() {
        byte[] bytes= new byte[23];
        UploadFileModel uploadFileModel=new UploadFileModel();
        uploadFileModel.setFileData(bytes);
       assertEquals(bytes,uploadFileModel.getFileData());
    }
}