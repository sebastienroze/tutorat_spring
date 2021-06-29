package ifa.devlog.tutorat.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.dao.QuestionDao;
import ifa.devlog.tutorat.dao.ReponseDao;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.model.Reponse;
import ifa.devlog.tutorat.model.Utilisateur;
import ifa.devlog.tutorat.security.JwtUtil;
import ifa.devlog.tutorat.utils.FichierService;
import ifa.devlog.tutorat.view.VueQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class QuestionController {

    QuestionDao questionDao;
    ReponseDao reponseDao;
    JwtUtil jwtUtil;
    private FichierService fichierService;

    @Autowired
    QuestionController(FichierService fichierService,QuestionDao questionDao,ReponseDao reponseDao, JwtUtil jwtUtil){
        this.questionDao = questionDao;
        this.reponseDao = reponseDao;
        this.jwtUtil = jwtUtil;
        this.fichierService = fichierService;
    }

    @GetMapping("/user/question/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable int id) {

        Optional<Question> question = questionDao.findById(id);

        if(question.isPresent()) {
            return ResponseEntity.ok(question.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @JsonView(VueQuestion.Standard.class)
    @GetMapping("/user/questions")
    public ResponseEntity<List<Question>> getQuestions(@RequestHeader(value="Authorization") String authorization) {
        boolean isAdmin = jwtUtil.getIsAdminFromAuthorization(authorization);
        if (isAdmin) return ResponseEntity.ok(questionDao.findAll());

        Integer idUtilisateur = jwtUtil.getUtilisateurIdFromAuthorization(authorization);
        Utilisateur utilisateur = new Utilisateur(idUtilisateur);
        return ResponseEntity.ok(questionDao.findByUtilisateur(utilisateur));
    }

    @PostMapping("/user/question")
    public ResponseEntity<String> addQuestion (
            @RequestBody Question question,
            @RequestHeader(value="Authorization") String authorization) {
        String token = authorization.substring(7);
        Integer idUtilisateur = jwtUtil.getTokenBody(token).get("id",Integer.class);
        boolean isAdmin = jwtUtil.getIsAdminFromAuthorization(authorization);
        if(question.getId() != null) {
            //on récupère la note actuelle dans la BDD
            Optional<Question> ancienneQuestion =  questionDao.findById(question.getId());
            //si la question n'existe pas
            if (!ancienneQuestion.isPresent()) {
                return ResponseEntity.badRequest().body("La question n'existe pas");
            }

            if (isAdmin) {
                question = ancienneQuestion.get();
                if (question.getReponse() ==null) {
                    question.setReponse(new Reponse());
                }
                if (question.getReponse().getDate_reponse()==null)  question.getReponse().setDate_reponse(LocalDate.now());
                question.setReponse(reponseDao.saveAndFlush(question.getReponse()));

            } else {
                //si il n'est pas l'auteur de la question ou si déjà répondu
                if ((ancienneQuestion.get().getUtilisateur().getId() != idUtilisateur)
                        || (ancienneQuestion.get().getReponse()!=null)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Vous ne pouvez pas éditer cette question");
                }
                question.setPhoto(ancienneQuestion.get().getPhoto());
                question.setOral(ancienneQuestion.get().getOral());
                question.setDate_question(ancienneQuestion.get().getDate_question());
                question.setUtilisateur(new Utilisateur(idUtilisateur));
                if (question.getDate_question()==null)  question.setDate_question(LocalDate.now());
            }
        }

        question = questionDao.saveAndFlush(question);
        return ResponseEntity.created(
                URI.create("/user/question/" + question.getId())
        ).build();
    }

    @DeleteMapping("/user/question/{id}")
    public ResponseEntity<Integer> deleteNoteListe (@PathVariable int id,@RequestHeader(value="Authorization") String authorization) {
        String token = authorization.substring(7);
        Integer idUtilisateur = jwtUtil.getTokenBody(token).get("id",Integer.class);
        boolean isAdmin = jwtUtil.getIsAdminFromAuthorization(authorization);

        Optional<Question> optionalQuestion =  questionDao.findById(id);
        //si la question n'existe pas
        if (!optionalQuestion.isPresent())
            return ResponseEntity.noContent().build();

        Question question = optionalQuestion.get();
        if (!isAdmin && (question.getUtilisateur().getId()!=idUtilisateur)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
        if (question.getPhoto()!=null) {
             fichierService.deleteFromLocalFileSystem(question.getPhoto());
        }
        if (question.getOral()!=null) {
            fichierService.deleteFromLocalFileSystem(question.getOral());
        }

        questionDao.deleteById(id);
        if (question.getReponse() != null) {
            if (question.getReponse().getPhoto()!=null) {
                fichierService.deleteFromLocalFileSystem(question.getReponse().getPhoto());
            }
            if (question.getReponse().getOral()!=null) {
                fichierService.deleteFromLocalFileSystem(question.getReponse().getOral());
            }
            reponseDao.deleteById(question.getReponse().getId());
        }
        return ResponseEntity.ok(id);
    }
}
