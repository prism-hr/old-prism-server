package uk.co.alumeni.prism.rest.dto.user;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

import javax.validation.constraints.Size;

public class UserContactDTO {

    @NotEmpty
    @Size(max = 100)
    private String name;

    @Email
    private String email;

    @PhoneNumber
    private String phone;

    @NotEmpty
    private String title;

    @NotEmpty
    @Size(max = 1000)
    private String content;

//    @NotEmpty
    private String recaptchaResponse;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecaptchaResponse() {
        return recaptchaResponse;
    }

    public void setRecaptchaResponse(String recaptchaResponse) {
        this.recaptchaResponse = recaptchaResponse;
    }
}
