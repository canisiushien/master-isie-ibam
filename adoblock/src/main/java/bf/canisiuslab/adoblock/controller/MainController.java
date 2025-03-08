package bf.canisiuslab.adoblock.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import bf.canisiuslab.adoblock.service.dto.KeysPair;
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
     * enregistre un document administratif sur la blockchain
     * 
     * 
     * @param documentAdministratif
     * @return
     * @throws Exception
     * @throws InvalidKeyException
     */
    @PostMapping(value = "/add-to-blockchain", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveDocumentToEthereum(@RequestParam("file") MultipartFile documentAdministratif,
            @RequestParam("privateKey") String privateKey, @RequestParam("publicKey") String publicKey)
            throws InvalidKeyException, Exception {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addDocumentToBlockchain(documentAdministratif, privateKey, publicKey));
    }

    /**
     * vérifie l'authenticité d'un document administratif depuis la blockchain
     * 
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidKeyException
     */
    @PostMapping(value = "/verify-from-blockchain", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> authenticateDocumentFromEthereum(@RequestParam("file") MultipartFile file)
            throws IOException, InvalidKeyException {

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.verifyDocumentFromBlockchain(file));

    }
}
