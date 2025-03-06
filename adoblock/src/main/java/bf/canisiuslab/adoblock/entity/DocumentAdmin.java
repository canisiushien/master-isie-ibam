package bf.canisiuslab.adoblock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Canisius <canisiushien@gmail.com>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "document_administratif", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"empreinteNumerique"}, name = "ux_empreintenumerique_in_documentadmin")})
public class DocumentAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nomFichier;

    @Column(columnDefinition = "TEXT") //Mapping du type TEXT dans Spring Boot : Spécifie le type TEXT pour PostgreSQL
    private String versionJson;

    @Column(nullable = false, length = 64)
    private String empreinteNumerique;

    private String signatureNumerique;

    private Instant dateChargement;
}
