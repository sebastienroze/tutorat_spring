package ifa.devlog.tutorat.controller;

import ifa.devlog.tutorat.dao.QuestionDao;
import ifa.devlog.tutorat.dao.ReponseDao;
import ifa.devlog.tutorat.model.Question;
import ifa.devlog.tutorat.model.Reponse;
import ifa.devlog.tutorat.model.Utilisateur;
import ifa.devlog.tutorat.security.JwtUtil;
import ifa.devlog.tutorat.utils.FichierService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin
public class FichierController {

    QuestionDao questionDao;
    ReponseDao reponseDao;
    JwtUtil jwtUtil;
    private FichierService fichierService;

    @Autowired
    FichierController(
            QuestionDao questionDao,
            ReponseDao reponseDao,
            JwtUtil jwtUtil,
            FichierService fichierService){

        this.questionDao = questionDao;
        this.reponseDao = reponseDao;
        this.jwtUtil = jwtUtil;
        this.fichierService = fichierService;
    }


    @GetMapping("/test/test")
    public  ResponseEntity<String> getTest(){
        return ResponseEntity.ok("test ok");
    }

    @ResponseBody
    @GetMapping(value = "/test/download/{nomDeFichier}")
    public ResponseEntity<byte[]> getImageAsResource(@PathVariable String nomDeFichier) {

        try {

            String mimeType = "";

            //verification nom de fichier
            if(!fichierService.nomFichierValide(nomDeFichier)) {
                System.out.println("nom de fichier incorrect, uniquement : - _ . lettres et chiffres (ex : pas de slash)");
                return ResponseEntity.notFound().build();
            }

            try {
                mimeType = Files.probeContentType(new File(nomDeFichier).toPath());


            //le fichier a une extension inconnue
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur : le fichier a une extension inconnue ou n'existe pas");
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();

            //note si mimeType est null c'est qe le fichier n'a pas d'extension ou n'existe pas
            if(mimeType != null) {
                headers.setContentType(MediaType.valueOf(mimeType));
            }

            headers.setCacheControl(CacheControl.noCache().getHeaderValue());

            byte[] media = fichierService.getFileFromUploadFolder(nomDeFichier);

            return new ResponseEntity<>(media, headers, HttpStatus.OK);


        } catch (FileNotFoundException e) {
            //Le fichier nom du fichier comporte un caractère non accepté ou le dossier d'upload est mal configuré
            //Voir application.properties : dossier.upload

            e.printStackTrace();
            System.out.println("Erreur : Le fichier est introuvable");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur : Le nom du fichier comporte un caractère non accepté ou le dossier d'upload est mal configuré");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }



    @PostMapping("/test/upload-fichier")
    public ResponseEntity<String> uploadMultipartFile(@RequestParam("file") MultipartFile file,
    @RequestParam("Authorization") MultipartFile multipartFileAuthorization,
    @RequestParam("questionID") Optional<MultipartFile> multipartFileQuestionID   ) {
        try {
            String authorization = multipartFileAuthorization.getOriginalFilename();
            String token = authorization.substring(7);
            Integer idUtilisateur = jwtUtil.getTokenBody(token).get("id",Integer.class);
            boolean isAdmin = jwtUtil.getIsAdminFromAuthorization(authorization);
            Question question = null;
            if (multipartFileQuestionID.isPresent()) {
                int idQuestion = Integer.parseInt(multipartFileQuestionID.get().getOriginalFilename());
                Optional<Question> optionalQuestion = questionDao.findById(idQuestion);
                if (optionalQuestion.isPresent()) {
                    question = optionalQuestion.get();
                    if ((question.getUtilisateur().getId() != idUtilisateur) && !isAdmin) {
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Interdit");
                    }
                }
            } else {
                question = new Question();
                question.setUtilisateur(new Utilisateur(idUtilisateur));
            }
            if (question.getReponse()!=null && !isAdmin) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body("Question déjà répondue");
            }
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String filename = FilenameUtils.removeExtension(file.getOriginalFilename());

            List<String> extensionAcceptePhoto = Arrays.asList(
                    "bmp", "gif", "png","jpg", "jpeg");
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString().replaceAll("-", ""));
            fileName.append("." + extension);

            if (!isAdmin) {
                if (extensionAcceptePhoto.contains(extension)) {
                    if (question.getPhoto() != null) {
                        fichierService.deleteFromLocalFileSystem(question.getPhoto());
                    }
                    question.setPhoto(fileName.toString());
                } else if ("3gp".equals(extension)) {
                    if (question.getOral() != null) {
                        fichierService.deleteFromLocalFileSystem(question.getOral());
                    }
                    question.setOral(fileName.toString());
                } else {
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                            .body("L'extension de ce fichier n'est pas prise en compte");
                }
            } else {
                if (question.getReponse() ==null) {
                    question.setReponse(new Reponse());
                }
                if (extensionAcceptePhoto.contains(extension)) {
                    if (question.getReponse().getPhoto() != null) {
                        fichierService.deleteFromLocalFileSystem(question.getReponse().getPhoto());
                    }
                    question.getReponse().setPhoto(fileName.toString());
                } else if ("3gp".equals(extension)) {
                    if (question.getReponse().getOral() != null) {
                        fichierService.deleteFromLocalFileSystem(question.getReponse().getOral());
                    }
                    question.getReponse().setOral(fileName.toString());
                } else {
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                            .body("L'extension de ce fichier n'est pas prise en compte");
                }
            }

            fichierService.uploadToLocalFileSystem(file, fileName.toString());

            if (question.getDate_question()==null)  question.setDate_question(LocalDate.now());
            Integer idRetour =null;
            if (question.getReponse() !=null) {
                question.setReponse(reponseDao.saveAndFlush(question.getReponse()));
                idRetour =  question.getReponse().getId();
            }
            question = questionDao.saveAndFlush(question);
            if (idRetour==null) idRetour = question.getId();
            return ResponseEntity.status(HttpStatus.OK).body(
                    "{\"id\":" +
                       idRetour.toString()
                    +",\"file\":\""
                    +fileName.toString()
                    + "\"}"
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Sauvegarde échouée");
        }
    }

/*
    @PostMapping(value = "/test/image-upload")
    @ResponseBody
    public ResponseEntity<String> saveBase64(@RequestBody String base64Str) {
        StringBuffer fileName = new StringBuffer();
        fileName.append(UUID.randomUUID().toString().replaceAll("-", ""));
        System.out.println(base64Str);
        if (base64Str.equals("")) {
            return ResponseEntity.badRequest().body("L'image est vide");
        } else if (base64Str.contains("data:image/png;")) {
            base64Str = base64Str.replace("data:image/png;base64,", "");
            fileName.append(".png");
        } else if (base64Str.contains("data:image/jpeg;")) {
            base64Str = base64Str.replace("data:image/jpeg;base64,", "");
            fileName.append(".jpeg");
        } else {
            //System.out.println("bad uppload");
            //fileName.append(".png");

            return ResponseEntity.badRequest().body("L'image doit être au format jpg ou png");

        }
//        System.out.println(base64Str);
        System.out.println("cree fichier");

        File file = new File("", fileName.toString());
        byte[] fileBytes = Base64.getUrlDecoder().decode(base64Str);
//        byte[] fileBytes = base64Str.getBytes();
        try {
            fichierService.uploadToLocalFileSystem(file, fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Sauvegarde échouée");
        }
        return ResponseEntity.ok().body("Sauvegarde réussie");
    }
*/
}

