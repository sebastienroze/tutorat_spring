package ifa.devlog.tutorat.model;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.view.VueNiveau;
import ifa.devlog.tutorat.view.VueQuestion;
import ifa.devlog.tutorat.view.VueUtilisateur;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)


public class Niveau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({VueNiveau.Standard.class, VueQuestion.Standard.class})
    private int id;
    @JsonView({VueNiveau.Standard.class,VueQuestion.Standard.class})
    private String denomination;
    public int getId() {
        return id;
    }
}
