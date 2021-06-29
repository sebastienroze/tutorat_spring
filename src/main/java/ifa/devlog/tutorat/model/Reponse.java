package ifa.devlog.tutorat.model;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.view.VueQuestion;
import ifa.devlog.tutorat.view.VueReponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)


public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({VueQuestion.Standard.class})
    private Integer id;
    @JsonView({VueQuestion.Standard.class})
    private String oral;
    @JsonView({VueQuestion.Standard.class})
    private String photo;
    @JsonView({VueQuestion.Standard.class})
    private LocalDate date_reponse;
    public Integer getId() {
        return id;
    }

    public String getOral() {
        return oral;
    }

    public void setOral(String oral) {
        this.oral = oral;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getDate_reponse() {
        return date_reponse;
    }

    public void setDate_reponse(LocalDate date_reponse) {
        this.date_reponse = date_reponse;
    }
}
