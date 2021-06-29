package ifa.devlog.tutorat.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FichierService {

    @Value("${dossier.upload}")
    private String dossierUpload;


    public String uploadToLocalFileSystem(File file, byte[] fileBytes) throws IOException {
        /* we will extract the file name (with extension) from the given file to store it in our local machine for now
        and later in virtual machine when we'll deploy the project
         */
        String fileName = StringUtils.cleanPath(file.getPath());

        InputStream targetStream = new ByteArrayInputStream(fileBytes);

        Path storageDirectory = Paths.get(dossierUpload);

        if (!Files.exists(storageDirectory)) { // if the folder does not exist
            try {
                Files.createDirectories(storageDirectory); // we create the directory in the given storage directory path
            } catch (Exception e) {
                e.printStackTrace();// print the exception
            }
        }
        Path destination = Paths.get(storageDirectory.toString() + "/" + fileName);
        Files.copy(targetStream, destination, StandardCopyOption.REPLACE_EXISTING);// we are Copying all bytes from an input stream to a file
        return fileName;
    }
    public void uploadToLocalFileSystem(byte[] fileBytes, String fileName) throws IOException {

        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        uploadToLocalFileSystem(inputStream, fileName);
    }

    public void uploadToLocalFileSystem(MultipartFile multipartFile, String fileName) throws IOException {

        uploadToLocalFileSystem(multipartFile.getInputStream(), fileName);
    }

    public void uploadToLocalFileSystem(InputStream inputStream, String fileName) throws IOException {

        Path storageDirectory = Paths.get(dossierUpload);

        if(!Files.exists(storageDirectory)){ // if the folder does not exist
            try {
                Files.createDirectories(storageDirectory); // we create the directory in the given storage directory path
            }catch (Exception e){
                e.printStackTrace();// print the exception
            }
        }

        Path destination = Paths.get(storageDirectory.toString() + "/" + fileName);

        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);// we are Copying all bytes from an input stream to a file

    }
    public void deleteFromLocalFileSystem(String fileName) {
        File file = new File(dossierUpload+"/"+fileName);
        file.delete();
    }

    public byte[] getFileFromUploadFolder(String fileName) throws IOException, FileNotFoundException {

        if(!nomFichierValide(fileName)) {
            throw new IOException("nom de fichier incorrect, uniquement : - _ . lettres et chiffres (pas de slash)");
        }

        Path destination = Paths.get(dossierUpload+"/"+fileName);// retrieve the image by its name

        try {
            return IOUtils.toByteArray(destination.toUri());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public boolean nomFichierValide(String fileName) {
        return fileName.matches("[-_A-Za-z0-9.]*");
    }

}