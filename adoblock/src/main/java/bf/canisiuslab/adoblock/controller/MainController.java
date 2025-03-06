package bf.canisiuslab.adoblock.controller;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import bf.canisiuslab.adoblock.service.dto.KeysPair;
import bf.canisiuslab.adoblock.utils.HashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import bf.canisiuslab.adoblock.service.MainService;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Canisius <canisiushien@gmail.com>
 */
@RestController
@RequestMapping("/api/adoblock")
public class MainController {

    private final MainService service;

    public MainController(MainService fileService) {
        this.service = fileService;
    }

    /**
     * génère une paire de clés cryptographiques
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    @GetMapping(path = "/generate-keys")
    public ResponseEntity<KeysPair> getKeysPair() throws NoSuchAlgorithmException {
        return ResponseEntity.status(HttpStatus.OK).body(service.generateKeysPair());
    }

    /**
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileType = file.getContentType();
            String content, hash;

            if (fileType.equals(HashUtil.PDF_TYPE)) {
                content = service.extractTextFromPdf(file);
            } else if (fileType.equals(HashUtil.WORD_TYPE)) {
                content = service.extractTextFromWord(file);
            } else {
                return ResponseEntity.badRequest().body("Type de fichier non supporté");
            }

            // calcul de l'empreinte numerique
            hash = HashUtil.calculateSHA256(content);

            // persistance en base
            service.saveDocument(content, hash, file.getOriginalFilename());

            // Convert to JSON
            Map<String, String> response = new HashMap<>();
            response.put("content", content);
            response.put("hash", hash);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur suivenue dans le traitement du fichier.");
        }
    }

    /**
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/verif", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> verifFile(@RequestParam("file") MultipartFile file) {
        try {
            String response;

            response = service.verifyTextFromFile(file);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur suivenue dans le traitement du fichier.");
        }
    }
}
