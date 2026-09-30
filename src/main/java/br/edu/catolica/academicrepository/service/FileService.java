package br.edu.catolica.academicrepository.service;

import br.edu.catolica.academicrepository.entity.FileMeta;
import br.edu.catolica.academicrepository.entity.Folder;
import br.edu.catolica.academicrepository.repository.FileRepository;
import br.edu.catolica.academicrepository.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {

    private final FileSearchService fileSearchService;
    private final FileRepository fileRepo;
    private final FolderRepository folderRepo;


    private final SupabaseStorageService supabaseStorageService;

    private final Path storageRoot = Paths.get("uploads");

    public FileService(FileRepository fileRepo, FolderRepository folderRepo, FileSearchService fileSearchService,
                       SupabaseStorageService supabaseStorageService) throws IOException {
        this.fileRepo = fileRepo;
        this.folderRepo = folderRepo;
        this.fileSearchService = fileSearchService;
        this.supabaseStorageService = supabaseStorageService;
        Files.createDirectories(storageRoot);
    }
    private String buildPathInBucket(Folder folder) {
        List<String> pathParts = new ArrayList<>();
        Folder current = folder;

        while (current != null) {
            pathParts.add(current.getName());
            current = current.getParent();
        }

        Collections.reverse(pathParts);

        return String.join("/", pathParts);
    }

    public FileMeta uploadFile(MultipartFile file, Long folderId, String author, String course, String advisor, String name) throws IOException {
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Pasta não encontrada"));

        String pathInBucket = buildPathInBucket(folder) + "/" + file.getOriginalFilename();

        supabaseStorageService.uploadFile(file, pathInBucket);

        FileMeta meta = new FileMeta();
        meta.setName(file.getOriginalFilename());
        meta.setSize(file.getSize());
        meta.setMimeType(file.getContentType());
        meta.setStoragePath(pathInBucket);
        meta.setFolder(folder);
        meta.setAuthor(author);
        meta.setCourse(course);
        meta.setAdvisor(advisor);
        meta.setName(name);

        fileSearchService.indexFile(meta);

        return fileRepo.save(meta);
    }

    public List<FileMeta> listFiles(Long folderId) {
        return fileRepo.findByFolder_Id(folderId);
    }

    public void zipFolder(Long folderId, OutputStream out) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
            zipRecursive(folderId, "", zipOut);
        }
    }

    private void zipRecursive(Long folderId, String basePath, ZipOutputStream zipOut) throws IOException {

        for (FileMeta file : fileRepo.findByFolder_Id(folderId)) {

            byte[] fileBytes = supabaseStorageService.downloadFile(file.getStoragePath());

            zipOut.putNextEntry(new ZipEntry(basePath + file.getName()));
            zipOut.write(fileBytes);
            zipOut.closeEntry();
        }

        for (Folder child : folderRepo.findByParent_Id(folderId)) {
            zipRecursive(child.getId(), basePath + child.getName() + "/", zipOut);
        }
    }
    public byte[] downloadFile(Long fileId) {
        FileMeta fileMeta = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado com ID: " + fileId));

        return supabaseStorageService.downloadUsingPublicUrl(fileMeta.getStoragePath());
    }

    public FileMeta deleteFile(Long fileID) throws IOException {
        FileMeta fileMeta = fileRepo.findById(fileID)
                .orElseThrow(() -> new RuntimeException("Arquivo nao encontrado com ID: " + fileID));

        supabaseStorageService.deleteFile(fileMeta.getStoragePath());

        fileSearchService.deleteById(fileID);

        fileRepo.delete(fileMeta);

        return fileMeta;
    }

    public FileMeta updateFile(
            Long fileId,
            String author,
            String course,
            String advisor,
            String name
    ) {

        FileMeta existing = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        if (author != null && !author.isBlank()) {
            existing.setAuthor(author);
        }

        if (course != null && !course.isBlank()) {
            existing.setCourse(course);
        }

        if (advisor != null && !advisor.isBlank()) {
            existing.setAdvisor(advisor);
        }

        if (name != null && !name.isBlank()) {
            existing.setName(name);
        }

        FileMeta saved = fileRepo.save(existing);

        fileSearchService.indexFile(saved);

        return saved;
    }


    public String getViewerUrl(Long fileId) {

        FileMeta meta = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        String publicUrl = supabaseStorageService.getPublicUrl(meta.getStoragePath());

        return "https://docs.google.com/gview?embedded=true&url=" + publicUrl;
    }

    public List<FileMeta> findFilesByCourse(String course) {
        return fileRepo.findByCourseIgnoreCase(course);
    }

}

