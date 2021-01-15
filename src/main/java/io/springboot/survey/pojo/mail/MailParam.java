package io.springboot.survey.pojo.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By: Vishal Jha
 * Date: 21/10/20
 * Time: 1:08 PM
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailParam {
    String sender;
    String recipient;
    String teamName;
    String surveyName;
}
