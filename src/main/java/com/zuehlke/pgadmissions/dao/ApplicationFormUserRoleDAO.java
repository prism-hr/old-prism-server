package com.zuehlke.pgadmissions.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormUserRoleDAO {

    private final SessionFactory sessionFactory;

    public ApplicationFormUserRoleDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormUserRoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public ApplicationFormUserRole get(Integer id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }

    public Date getUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("role", "applicationRole")
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.ge("applicationRole.updateVisibility", updateVisibility))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("updateTimestamp"))).uniqueResult();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.in("role.id", authorities)).list();
    }

    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthorities(ApplicationForm applicationForm, RegisteredUser user, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthoritiesWithActions(ApplicationForm applicationForm, RegisteredUser user, List<Authority> authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }
    
    public Boolean getRaisesUrgentFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUrgentFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUrgentFlag"))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("raisesUrgentFlag"))).uniqueResult();
        return BooleanUtils.toBoolean(raisesUrgentFlag);
    }

    public Boolean getRaisesUpdateFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUpdateFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUpdateFlag"))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("raisesUpdateFlag"))).uniqueResult();
        return BooleanUtils.toBoolean(raisesUpdateFlag);
    }

    public void deleteApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SP_DELETE_APPLICATION_UPDATE(?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId()).executeUpdate();
    }
    
    public void deleteApplicationRole(ApplicationForm application, RegisteredUser user, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ROLE(?, ?, ?);")
                .setInteger(0, application.getId())
                .setInteger(1, user.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void deleteProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void deleteUserRole(RegisteredUser registeredUser, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void insertApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser, Date updateTimestamp,
            ApplicationUpdateScope updateVisibility) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_APPLICATION_UPDATE(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, javaDateToMySQLDateString(updateTimestamp))
                .setInteger(3, ApplicationUpdateScope.valueOf(updateVisibility.toString()).ordinal()).executeUpdate();
    }

    public void insertProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void insertUserRole(RegisteredUser registeredUser, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void updateApplicationDueDate(ApplicationForm applicationForm, Date deadlineTimestamp) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_FORM_DUE_DATE(?, ?);")
                .setInteger(0, applicationForm.getId()).setDate(1, deadlineTimestamp).executeUpdate();
    }

    public void updateApplicationInterest(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean interested) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_INTEREST(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setBoolean(2, interested).executeUpdate();
    }
    
    public void updateUrgentApplications() {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_URGENT_APPLICATIONS();").executeUpdate();
    }

    private String javaDateToMySQLDateString(Date date) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return outputDateFormat.format(date);
    }

}
