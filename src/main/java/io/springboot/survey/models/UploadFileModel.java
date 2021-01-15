package io.springboot.survey.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * This model contains data/file uploaded by the user while giving response to a io.springboot.survey.It includes file name,
 * file type, file data
 */
@Entity
@Table(name="file_upload")
@Getter
@Setter
@NoArgsConstructor
public class UploadFileModel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "file_id", strategy = "uuid2")
    @ApiModelProperty(notes = "Auto generated file id",example = "76")
    private String fileId;

    @Column(name = "file_name",length = 50)
    @ApiModelProperty(notes = "file name",example = "review")
    private String fileName;

    @Column(name="file_type")
    @ApiModelProperty(notes = "file type",example = "jpeg")
    private String fileType;

    @Column(name="file_data")
    private byte[] fileData;
}
