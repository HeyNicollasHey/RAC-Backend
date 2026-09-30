package br.edu.catolica.academicrepository.controller;

import br.edu.catolica.academicrepository.entity.FileMeta;
import br.edu.catolica.academicrepository.repository.FileRepository;
import br.edu.catolica.academicrepository.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @Autowired
    private FileRepository fileRepo;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMeta> upload(@RequestParam("file") MultipartFile file, @RequestParam Long folderId, @RequestParam String author, @RequestParam String course, @RequestParam String advisor, @RequestParam String name) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file,folderId,author,course,advisor,name));
    }

    @GetMapping("/{folderId}")
    public List<FileMeta> listFiles(@PathVariable Long folderId) {
        return fileService.listFiles(folderId);
    }

    @GetMapping("/download-zip-folder/{folderId}")
    public ResponseEntity<byte[]> downloadZip(@PathVariable Long folderId) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fileService.zipFolder(folderId, baos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=folder.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }

    @GetMapping("/download-file/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) throws IOException {
        FileMeta fileMeta = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado com ID: " + fileId));

        byte[] fileBytes = fileService.downloadFile(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileMeta.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    @DeleteMapping("/delete-file/{fileId}")
    public ResponseEntity<FileMeta> deleteFile(@PathVariable Long fileId) throws IOException {
        FileMeta fileMeta = fileRepo.findById(fileId).orElseThrow(() -> new RuntimeException("Arquivo nao encontrado com o ID: " + fileId));
        return ResponseEntity.ok(fileService.deleteFile(fileId));
    }

    @PutMapping("/files/{id}")
    public ResponseEntity<FileMeta> updateFile(
            @PathVariable Long id,
            @RequestBody FileMeta dto
    ) throws IOException {

        FileMeta updated = fileService.updateFile(
                id,
                dto.getAuthor(),
                dto.getCourse(),
                dto.getAdvisor(),
                dto.getName()
        );

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/file-info/{fileId}")
    public ResponseEntity<FileMeta> getFileInfo(@PathVariable Long fileId) {
        FileMeta fileMeta = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));
        return ResponseEntity.ok(fileMeta);
    }

    @GetMapping("/files/view/{id}")
    public ResponseEntity<String> viewFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getViewerUrl(id));
    }

    @GetMapping("/by-course")
    public ResponseEntity<List<FileMeta>> getFilesByCourse(@RequestParam String course) {
        List<FileMeta> files = fileService.findFilesByCourse(course);
        return ResponseEntity.ok(files);
    }

}
