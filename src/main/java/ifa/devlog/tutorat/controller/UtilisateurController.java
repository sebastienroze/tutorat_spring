package ifa.devlog.tutorat.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.dao.UtilisateurDao;
import ifa.devlog.tutorat.model.Role;
import ifa.devlog.tutorat.model.Utilisateur;
import ifa.devlog.tutorat.security.JwtUtil;
import ifa.devlog.tutorat.security.UserDetailsCustom;
import ifa.devlog.tutorat.security.UserDetailsServiceCustom;
import ifa.devlog.tutorat.utils.FichierService;
import ifa.devlog.tutorat.view.VueUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class UtilisateurController {

    private UtilisateurDao utilisateurDao;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private UserDetailsServiceCustom userDetailsServiceCustom;
    private PasswordEncoder passwordEncoder;
    private FichierService fichierService;

    @Autowired
    UtilisateurController(
            UtilisateurDao utilisateurDao,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            UserDetailsServiceCustom userDetailsServiceCustom,
            PasswordEncoder passwordEncoder,
            FichierService fichierService
    ) {
        this.utilisateurDao = utilisateurDao;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsServiceCustom = userDetailsServiceCustom;
        this.passwordEncoder = passwordEncoder;
        this.fichierService = fichierService;
    }

    @PostMapping("/authentification")
    public ResponseEntity<String> authentification(@RequestBody Utilisateur utilisateur) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            utilisateur.getPseudo(), utilisateur.getMotDePasse()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Mauvais pseudo / mot de passe");
        }

        UserDetailsCustom userDetails = this.userDetailsServiceCustom.loadUserByUsername(utilisateur.getPseudo());

        return ResponseEntity.ok(jwtUtil.generateToken(userDetails));
    }

    /*
    @PostMapping("/inscription")
    public ResponseEntity<String> inscription(@RequestBody Utilisateur utilisateur) {

        Optional<Utilisateur> utilisateurDoublon = utilisateurDao.trouverParPseudo(utilisateur.getPseudo());

        if (utilisateurDoublon.isPresent()) {
            return ResponseEntity.badRequest().body("Ce pseudo est déja utilisé");
        } else {

            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

            Role roleUtilisateur = new Role();
            roleUtilisateur.setId(1);

            utilisateur.getListeRole().add(roleUtilisateur);

            utilisateurDao.saveAndFlush(utilisateur);

            return ResponseEntity.ok(Integer.toString(utilisateur.getId()));
        }
    }
*/
    @PostMapping("/admin/utilisateur")
    public ResponseEntity<String> updateUser(@RequestBody Utilisateur utilisateur) {

        Optional<Utilisateur> utilisateurBddOptional = utilisateurDao.findById(utilisateur.getId());

        if (utilisateurBddOptional.isPresent()) {
            Utilisateur utilisateurBdd = utilisateurBddOptional.get();
            utilisateur.setMotDePasse(utilisateurBdd.getMotDePasse());
            utilisateurDao.save(utilisateur);
            return ResponseEntity.ok().body("Utilisateur mis à jour");
        }

        return ResponseEntity.notFound().build();
    }

    @JsonView(VueUtilisateur.Standard.class)
    @GetMapping("/user/utilisateur-connecte")
    public ResponseEntity<Utilisateur> getInformationUtilisateurConnecte(
            @RequestHeader(value = "Authorization") String authorization) {
        //la valeur du champs authorization est extrait de l'entête de la requête

        //On supprime la partie "Bearer " de la valeur de l'authorization
        System.out.println("utilisateur connecte");
        String token = authorization.substring(7);
        System.out.println(token);

        //on extrait l'information souhaitée du token
        String username = jwtUtil.getTokenBody(token).getSubject();
        System.out.println(username);

//        Optional<Utilisateur> utilisateur = utilisateurDao.trouverParPseudo(username);
        Optional<Utilisateur> utilisateur = utilisateurDao.findByPseudo(username);

        if (utilisateur.isPresent()) {
            System.out.println("utilisateur present");

            return ResponseEntity.ok().body(utilisateur.get());
        }

        return ResponseEntity.notFound().build();
    }

    @JsonView(VueUtilisateur.Standard.class)
    @GetMapping("/admin/utilisateur/{id}")
    public ResponseEntity<Utilisateur> getUtilisateur(@PathVariable int id) {

        Optional<Utilisateur> utilisateur = utilisateurDao.findById(id);

        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @JsonView(VueUtilisateur.Standard.class)
    @GetMapping("/admin/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getUtilisateurs() {

        return ResponseEntity.ok(utilisateurDao.findAll());
    }

    @DeleteMapping("/admin/utilisateur/{id}")
    public ResponseEntity<Integer> deleteUtilisateur(@PathVariable int id) {

        if (utilisateurDao.existsById(id)) {
            utilisateurDao.deleteById(id);
            return ResponseEntity.ok(id);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}





