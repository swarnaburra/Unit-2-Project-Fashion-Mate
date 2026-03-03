package fashionmate_backend.repositories;

import fashionmate_backend.models.StyleLens;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.Style;
import java.util.List;

public interface StyleLensRepository extends JpaRepository<StyleLens, Long> {

    List<StyleLens> findByUserId(Long userId);
}
