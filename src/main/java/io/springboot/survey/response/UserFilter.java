package io.springboot.survey.response;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserFilter {
    private List<String> gender;
    private List<String> role;
    private List<String> designation;
    private List<String> project;
    private List<String> status;
    private String roleName;

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
