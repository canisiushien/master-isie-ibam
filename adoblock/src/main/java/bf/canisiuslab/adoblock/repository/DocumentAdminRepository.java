package bf.canisiuslab.adoblock.repository;

import bf.canisiuslab.adoblock.entity.DocumentAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentAdminRepository extends JpaRepository<DocumentAdmin, Long> {
    Optional<DocumentAdmin> findByEmpreinteNumerique(String hash);
}
