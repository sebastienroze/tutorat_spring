package ifa.devlog.tutorat.dao;

import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {
    public List<Question> findByUtilisateur(Utilisateur utilisateur) ;
}
