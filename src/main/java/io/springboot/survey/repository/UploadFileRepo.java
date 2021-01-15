package io.springboot.survey.repository;

import io.springboot.survey.models.UploadFileModel;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UploadFileRepo extends PagingAndSortingRepository<UploadFileModel, Integer> {
    UploadFileModel findByFileId(String fileId);
}
