package uk.co.alumeni.prism.domain.workflow;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;

import com.google.common.collect.Sets;

@Entity
@Table(name = "state_action_assignment", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "role_id", "external_mode" }) })
public class StateActionAssignment implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "external_mode", nullable = false)
    private Boolean externalMode;

    @Column(name = "action_enhancement")
    @Enumerated(EnumType.STRING)
    private PrismActionEnhancement actionEnhancement;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "state_action_recipient", joinColumns = { @JoinColumn(name = "state_action_assignment_id", nullable = false) }, inverseJoinColumns = {
            @JoinColumn(name = "role_id", nullable = false) })
    private Set<Role> recipients = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateAction getStateAction() {
        return stateAction;
    }

    public void setStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    public Set<Role> getRecipients() {
        return recipients;
    }

    public StateActionAssignment withStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
        return this;
    }

    public StateActionAssignment withRole(Role role) {
        this.role = role;
        return this;
    }

    public StateActionAssignment withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    public StateActionAssignment withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    public StateActionAssignment addRecipient(Role recipient) {
        recipients.add(role);
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("stateAction", stateAction).addProperty("role", role).addProperty("externalMode", externalMode);
    }

}
