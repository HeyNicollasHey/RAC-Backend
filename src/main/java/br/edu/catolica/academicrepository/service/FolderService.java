package br.edu.catolica.academicrepository.service;

import br.edu.catolica.academicrepository.entity.FileMeta;
import br.edu.catolica.academicrepository.entity.Folder;
import br.edu.catolica.academicrepository.repository.FileRepository;
import br.edu.catolica.academicrepository.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    private final FolderRepository folderRepo;

    private final FolderSearchService folderSearchService;

    private final FileRepository fileRepo;

    private final SupabaseStorageService supabaseStorageService;

    public FolderService(FolderRepository folderRepo, FolderSearchService folderSearchService, FileRepository fileRepo,
                         SupabaseStorageService supabaseStorageService) {
        this.folderRepo = folderRepo;
        this.folderSearchService = folderSearchService;
        this.fileRepo = fileRepo;
        this.supabaseStorageService = supabaseStorageService;
    }

    public Folder createFolder(String name, Long parentId, String course) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setCourse(course);
        if (parentId != null) {
            folder.setParent(folderRepo.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Pasta não encontrada")));
        }
        folderSearchService.indexFolder(folder);
        return folderRepo.save(folder);
    }

    public List<Folder> listChildren(Long parentId) {
        return folderRepo.findByParent_Id(parentId);
    }

    public Optional<Folder> findFolder(Long folderId){
        return folderRepo.findById(folderId);
    }

    public Folder updateFolder(Long folderId, String newName, String newCourse) {

        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Pasta não encontrada"));

        if (newName != null) folder.setName(newName);
        if (newCourse != null) folder.setCourse(newCourse);

        Folder saved = folderRepo.save(folder);
        folderSearchService.indexFolder(saved);

        return saved;
    }

    public void deleteFolder(Long folderId) {

        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Pasta não encontrada"));

        deleteFolderRecursive(folder);

        folderSearchService.deleteById(folderId);
        folderRepo.deleteById(folderId);
    }

    private void deleteFolderRecursive(Folder folder) {

        List<FileMeta> files = fileRepo.findByFolder_Id(folder.getId());
        for (FileMeta f : files) {
            supabaseStorageService.deleteFile(f.getStoragePath());
            fileRepo.delete(f);
        }

        List<Folder> children = folderRepo.findByParent_Id(folder.getId());
        for (Folder child : children) {
            deleteFolderRecursive(child);
        }
    }

    public List<Folder> findFoldersByCourse(String course) {
        return folderRepo.findByCourseIgnoreCase(course);
    }



}
