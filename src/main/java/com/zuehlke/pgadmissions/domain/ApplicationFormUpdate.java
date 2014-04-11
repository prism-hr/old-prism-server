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

    public ApplicationFormUpdate(ApplicationForm applicationForm, User user, Boolean raisesUpdateFlag) {
        setId(applicationForm, user);
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    public ApplicationFormUpdatePrimaryKey getId() {
        return id;
    }

    public void setId(ApplicationForm applicationForm, User user) {
        this.id.setApplicationForm(applicationForm);
        this.id.setUser(user);
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
        protected User user;

        public ApplicationFormUpdatePrimaryKey() {
        }

        public ApplicationFormUpdatePrimaryKey(ApplicationForm applicationForm, User user) {
            this.applicationForm = applicationForm;
            this.user = user;
        }

        public ApplicationForm getApplicationForm() {
            return applicationForm;
        }

        public void setApplicationForm(ApplicationForm applicationForm) {
            this.applicationForm = applicationForm;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationForm, user);
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
            return Objects.equal(applicationForm.getId(), other.getApplicationForm().getId()) && Objects.equal(user.getId(), other.getUser().getId());
        }

    }

}
