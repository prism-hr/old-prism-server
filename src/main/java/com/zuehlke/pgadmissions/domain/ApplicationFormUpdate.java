package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.base.Objects;

@Entity
@Table(name = "APPLICATION_FORM_UPDATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ApplicationFormUpdate implements Serializable {

    private static final long serialVersionUID = -1803235075506702201L;

    @EmbeddedId
    private ApplicationFormUpdatePrimaryKey id;

    @Column(name = "raises_update_flag")
    private Boolean raisesUpdateFlag;
    
    public ApplicationFormUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean raisesUpdateFlag) {
        setId(applicationForm, registeredUser);
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    public ApplicationFormUpdatePrimaryKey getId() {
        return id;
    }

    public void setId(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        this.id.setApplicationForm(applicationForm);
        this.id.setRegisteredUser(registeredUser);
    }

    public Boolean getRaisesUpdateFlag() {
        return raisesUpdateFlag;
    }

    public void setRaisesUpdateFlag(Boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    @Embeddable
    public static class ApplicationFormUpdatePrimaryKey implements Serializable {

        private static final long serialVersionUID = 2062159354895542411L;

        @Column(name = "application_form_id")
        protected ApplicationForm applicationForm;

        @Column(name = "registered_user_id")
        protected RegisteredUser registeredUser;

        public ApplicationFormUpdatePrimaryKey() {
        }

        public ApplicationFormUpdatePrimaryKey(ApplicationForm applicationForm, RegisteredUser registeredUser) {
            this.applicationForm = applicationForm;
            this.registeredUser = registeredUser;
        }

        public ApplicationForm getApplicationForm() {
            return applicationForm;
        }

        public void setApplicationForm(ApplicationForm applicationForm) {
            this.applicationForm = applicationForm;
        }

        public RegisteredUser getRegisteredUser() {
            return registeredUser;
        }

        public void setRegisteredUser(RegisteredUser registeredUser) {
            this.registeredUser = registeredUser;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationForm, registeredUser);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ApplicationFormUpdatePrimaryKey other = (ApplicationFormUpdatePrimaryKey) obj;
            return Objects.equal(applicationForm.getId(), other.getApplicationForm().getId())
                    && Objects.equal(registeredUser.getId(), other.getRegisteredUser().getId());
        }

    }

}
