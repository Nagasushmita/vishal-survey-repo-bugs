package io.springboot.survey.models;


import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static io.springboot.survey.utils.Constants.ModelConstraintMessage.ROLE_NAME_EMPTY;
import static io.springboot.survey.utils.Constants.ModelConstraintMessage.ROLE_NAME_NULL;

/**
 * This model holds the data about the roles, it includes role id,role name ,created by
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class RoleModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "role_id")
    @ApiModelProperty(notes = "Auto generated role id",example = "1")
    private int roleId;

    @Column(name = "role",length = 20)
    @NotNull(message = ROLE_NAME_NULL)
    @NotEmpty(message = ROLE_NAME_EMPTY)
    @ApiModelProperty(notes = "Role name",example = "HR|Manager|Employee")
    private String role;

    @Column(name = "created_by")
    @ApiModelProperty(notes = "Id of the person who created the role",example = "56")
    private Integer createdBy;

    @Column(name = "creation_on")
    @ApiModelProperty(notes = "time of creation",example = "1577094529")
    private long createdOn;


    @Getter(AccessLevel.NONE)
    @JoinColumn(name ="role_id",unique = true)
    @OneToOne(cascade =CascadeType.ALL)
    private PrivilegesModel privilegesModel;

    public RoleModel(int roleId, String role) {
        super();
        this.roleId = roleId;
        this.role  = role;
    }


}