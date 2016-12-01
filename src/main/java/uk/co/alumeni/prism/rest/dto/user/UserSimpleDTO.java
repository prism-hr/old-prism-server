package uk.co.alumeni.prism.rest.dto.user;

import javax.validation.constraints.Size;

public class UserSimpleDTO extends UserDTO {

    @Size(max = 30)
    private String firstName2;

    @Size(max = 30)
    private String firstName3;

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

}
