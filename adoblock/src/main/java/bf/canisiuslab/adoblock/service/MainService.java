package bf.canisiuslab.adoblock.service;

import bf.canisiuslab.adoblock.service.dto.KeysPair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Canisius <canisiushien@gmail.com>
 */
public interface MainService {

    /**
     * genere une paire de clés cryptographiques
     *
     * @return
     */
    KeysPair generateKeysPair() throws NoSuchAlgorithmException;

    /**
     * extrait du texte brut dans un fichier .pdf
     *
     * @param file
     * @return
     */
    String extractTextFromPdf(MultipartFile file) throws IOException;

    /**
     * extrait du texte brut dans un fichier .docx
     *
     * @param file
     * @return
     */
    String extractTextFromWord(MultipartFile file) throws IOException;

    /**
     *
     * @param content
     * @param hash
     * @param fileName
     */
    void saveDocument(String content, String hash, String fileName);

    /**
     * extrait du texte brut dans un fichier, calcule le hash et verifie que
     * fichier en question est valide
     *
     * @param file
     * @return
     * @throws IOException
     */
    String verifyTextFromFile(MultipartFile file) throws IOException;
}
