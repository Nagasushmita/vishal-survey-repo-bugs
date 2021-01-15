package io.springboot.survey.pojo.scheduling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDetail;

import java.time.ZonedDateTime;

/**
 * Created By: Vishal Jha
 * Date: 20/10/20
 * Time: 5:45 PM
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyMonthlyTriggerParam {
    JobDetail jobDetail;
    @NotNull ZonedDateTime startAt;
    @NotNull ZonedDateTime endAt;
    int number;
    int hour;
    int minute;
}
