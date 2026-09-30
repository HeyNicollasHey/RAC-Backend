package br.edu.catolica.academicrepository.repository;

import br.edu.catolica.academicrepository.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByParent_Id(Long parentId);

    List<Folder> findByCourseIgnoreCase(String course);
}