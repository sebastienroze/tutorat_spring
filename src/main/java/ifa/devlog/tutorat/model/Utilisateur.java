package ifa.devlog.tutorat.model;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.view.VueQuestion;
import ifa.devlog.tutorat.view.VueUtilisateur;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Utilisateur {

    public Utilisateur() {
    }

    public Utilisateur(Integer id) {
        this.id = id;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({VueUtilisateur.Standard.class, VueQuestion.Standard.class})
    private int id;

    @Column(nullable = false, length = 50)
    @JsonView({VueUtilisateur.Standard.class,VueQuestion.Standard.class})
    private String pseudo;

    private String motDePasse;

//    @JsonView(VueUtilisateur.Standard.class)
    @OneToMany(mappedBy = "utilisateur")
    private List<Question> listeQuestion;

    @ManyToMany
    @JsonView({VueUtilisateur.Standard.class})
    @JoinTable(
            name = "utilisateur_role",
            joinColumns = @JoinColumn(name = "utilisateur_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> listeRole = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Set<Role> getListeRole() {
        return listeRole;
    }

    public void setListeRole(Set<Role> listeRole) {
        this.listeRole = listeRole;
    }

/*    public boolean isAdmin() {
        for (Role role : getListeRole() ) {
            if ("ROLE_ADMINISTRATEUR".equals(role.getDenomination()) {
                return true;
            }
        }
        return false;
    }
*/
    public List<Question> getListeQuestion() {
      /*  if (isAdmin()) {
            List<Question> listeToutesQuestion;
            listeToutesQuestion = que

            return listeToutesQuestion;
        }*/
        return listeQuestion;
    }

    public void setListeNote(List<Question> listeQuestions) {
        this.listeQuestion = listeQuestions;
    }
}
