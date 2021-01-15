package io.springboot.survey.service;

import io.springboot.survey.request.ScheduleEmailRequest;
import io.springboot.survey.response.ScheduleEmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SchedulingService {
    ResponseEntity<ScheduleEmailResponse> scheduleEmail(ScheduleEmailRequest scheduleEmailRequest);
}
