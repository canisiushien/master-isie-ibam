package bf.canisiuslab.adoblock.service.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO pour la paire de clés
 *
 * @author Canisius <canisiushien@gmail.com>
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVerifDTO {

    private boolean isAuthenticated = false; // le doc est-il valide/authentique ?

    private boolean isIntegrated = true; // le contenu du doc est-il au moins integre ?

    private String dateAddToEthereum; // date a laquelle le document a ete ajoute a la blockchain (horodatage)

    private String fileName; // nom du fichier soumis a authentification (Document numetique)

    private String docHashed; // l'empreinte numerique du doc. Ce hash est encode en base64 (Empreinte
                              // numerique du document)
    private Instant requestDate; // date de demande d'authentification (Date de demande)

    private String typeKey; // type d'algo cryptographique

    private String ellipticCurve; // courbe elliptique

    private String publicKey; // cle publique associee
}