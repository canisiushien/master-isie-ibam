package bf.canisiuslab.adoblock.utils;

import lombok.extern.slf4j.Slf4j;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Canisius <canisiushien@gmail.com>
 */
@Slf4j
public class HashUtil {

    public static String PDF_TYPE = "application/pdf";

    public static String WORD_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static String calculateSHA256(String text) {
        try {
            // Initialiser le calculateur SHA-256
            //Java fournit une implémentation native de SHA-256 via la classe MessageDigest. Donc, pas besoin de nouvelle dependance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convertir le texte en tableau d'octets
            byte[] hashBytes = digest.digest(text.getBytes());

            // Convertir les octets en une chaîne hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // Ajouter un zéro pour les valeurs < 0x10
                hexString.append(hex);
            }

            return hexString.toString(); // Retourner le hash sous forme de chaîne
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm SHA-256 introuvable : ", e);
        }
    }
}
