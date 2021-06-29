package ifa.devlog.tutorat.model;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.view.VueQuestion;
import ifa.devlog.tutorat.view.VueUtilisateur;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)

public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @JsonView({VueQuestion.Standard.class,VueUtilisateur.Standard.class})
    @JsonView({VueQuestion.Standard.class})
    private Integer id;
    @JsonView({VueQuestion.Standard.class})
    private String sujet;
    @JsonView({VueQuestion.Standard.class})
    private String explication;
    @JsonView({VueQuestion.Standard.class})
    private String oral;
    @JsonView({VueQuestion.Standard.class})
    private String photo;
    @JsonView({VueQuestion.Standard.class})
    private LocalDate date_question;
    @ManyToOne
    @JsonView({VueQuestion.Standard.class})
    private Utilisateur utilisateur;
    @ManyToOne
    @JsonView({VueQuestion.Standard.class})
    private Niveau niveau;
    @ManyToOne
    @JsonView({VueQuestion.Standard.class})
    private Reponse reponse;
    public Integer getId() {
        return id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public LocalDate getDate_question() {
        return date_question;
    }

    public void setDate_question(LocalDate date_question) {
        this.date_question = date_question;
    }

    public String getOral() {
        return oral;
    }

    public void setOral(String oral) {
        this.oral = oral;
    }

    public Reponse getReponse() {
        return reponse;
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
    }
}
