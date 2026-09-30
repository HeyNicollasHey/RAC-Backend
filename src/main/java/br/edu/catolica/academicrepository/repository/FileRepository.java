package br.edu.catolica.academicrepository.repository;

import br.edu.catolica.academicrepository.entity.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<FileMeta, Long> {
    List<FileMeta> findByFolder_Id(Long folderId);

    List<FileMeta> findByCourseIgnoreCase(String course);
}
