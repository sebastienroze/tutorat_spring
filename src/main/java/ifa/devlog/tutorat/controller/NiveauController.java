package ifa.devlog.tutorat.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ifa.devlog.tutorat.dao.NiveauDao;
import ifa.devlog.tutorat.model.Niveau;
import ifa.devlog.tutorat.model.Utilisateur;
import ifa.devlog.tutorat.security.JwtUtil;
import ifa.devlog.tutorat.view.VueNiveau;
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
public class NiveauController {

    NiveauDao niveauDao;
    JwtUtil jwtUtil;

    @Autowired
    NiveauController(NiveauDao niveauDao, JwtUtil jwtUtil){
        this.niveauDao = niveauDao;
        this.jwtUtil = jwtUtil;
    }

    @JsonView(VueNiveau.Standard.class)
    @GetMapping("/user/niveaux")
    public ResponseEntity<List<Niveau>> getNiveaux(@RequestHeader(value="Authorization") String authorization) {
        return ResponseEntity.ok(niveauDao.findAll());
    }

}






