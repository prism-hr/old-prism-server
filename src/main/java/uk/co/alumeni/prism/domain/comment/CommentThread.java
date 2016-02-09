package uk.co.alumeni.prism.domain.comment;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = "comment_thread")
public class CommentThread {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @OneToMany(mappedBy = "thread")
    private Set<Comment> comments = Sets.newHashSet();
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
}
