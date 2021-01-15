package io.springboot.survey.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrivilegeSet {
    String name;
    Boolean value;
    String text;

    public PrivilegeSet(String name, Boolean value, String text) {
        this.name = name;
        this.value = value;
        this.text = text;
    }
}
