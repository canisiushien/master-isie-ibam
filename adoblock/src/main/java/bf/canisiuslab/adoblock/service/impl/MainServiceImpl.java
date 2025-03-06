package bf.canisiuslab.adoblock.service.impl;

import bf.canisiuslab.adoblock.entity.DocumentAdmin;
import bf.canisiuslab.adoblock.repository.DocumentAdminRepository;
import bf.canisiuslab.adoblock.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.security.*;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.ECGenParameterSpec;
import java.io.*;

import java.io.IOException;
import java.time.Instant;
import bf.canisiuslab.adoblock.service.MainService;
import bf.canisiuslab.adoblock.service.dto.KeysPair;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Canisius <canisiushien@gmail.com>
 */
@Slf4j
@Service
public class MainServiceImpl implements MainService {

    private final DocumentAdminRepository documentAdminRepository;

    public MainServiceImpl(DocumentAdminRepository documentAdminRepository) {
        this.documentAdminRepository = documentAdminRepository;
    }

    @Override
    public KeysPair generateKeysPair() throws NoSuchAlgorithmException {
        log.info("Generation de paire de clés ECDSA : {}");
        KeysPair keysPair = new KeysPair();
        try {
            // Génération d'une paire de clés ECDSA (Courbe P-256)
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new ECGenParameterSpec("secp256r1")); // P-256
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            keysPair.setPrivateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            keysPair.setPublicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            keysPair.setTypeKey("ECDSA");
            keysPair.setEllipticCurve("secp256r1 (P-256)");

        } catch (InvalidAlgorithmParameterException ex) {
            throw new RuntimeException("Une erreur s'est produite lors de la génération des clés. \n" + ex);
        }

        return keysPair;
    }

    /**
     * extrait et retourne du texte brut contenu dans fichier PDF
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        log.info("Extraction du contenu d'un PDF textuel : {}", file.getOriginalFilename());
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        return text;
    }

    /**
     * extrait et retourne du texte brut contenu dans fichier Word
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String extractTextFromWord(MultipartFile file) throws IOException {
        log.info("Extraction du contenu d'un word : {}", file.getOriginalFilename());
        XWPFDocument document = new XWPFDocument(file.getInputStream());
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        String text = extractor.getText();
        document.close();

        return text;
    }

    /**
     * verifie si le document est valide
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String verifyTextFromFile(MultipartFile file) throws IOException {
        String fileType = file.getContentType();
        String content;
        log.info("Verification de la validité du document : {}", file.getOriginalFilename());

        if (fileType.equals(HashUtil.PDF_TYPE)) {
            content = this.extractTextFromPdf(file);
        } else if (fileType.equals(HashUtil.WORD_TYPE)) {
            content = this.extractTextFromWord(file);
        } else {
            throw new RuntimeException("Type de fichier non supporté. Veuillez réessayer SVP");
        }

        // Calculer l'empreinte numérique
        String hash = HashUtil.calculateSHA256(content);
        DocumentAdmin response = documentAdminRepository.findByEmpreinteNumerique(hash).orElse(null);

        return (response == null ? "Document non reconnu." : "Document valide (données intègres).");
    }

    /**
     *
     * @param texte
     * @param empreinte
     * @param fileName
     */
    @Override
    public void saveDocument(String texte, String empreinte, String fileName) {
        DocumentAdmin documentAdmin = new DocumentAdmin();

        documentAdmin.setDateChargement(Instant.now());
        documentAdmin.setNomFichier(fileName);
        documentAdmin.setVersionJson(texte);
        documentAdmin.setEmpreinteNumerique(empreinte);
        documentAdmin.setSignatureNumerique(null);
        documentAdminRepository.save(documentAdmin);
    }

}
