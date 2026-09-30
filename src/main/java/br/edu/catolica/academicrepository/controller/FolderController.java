package br.edu.catolica.academicrepository.controller;

import br.edu.catolica.academicrepository.entity.Folder;
import br.edu.catolica.academicrepository.entity.FolderUpdateDTO;
import br.edu.catolica.academicrepository.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/folders")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/create-folder")
    public ResponseEntity<Folder> createFolder(@RequestParam String name, @RequestParam(required = false) Long parentId,
                                               @RequestParam String course) {
        Folder folder = folderService.createFolder(name, parentId, course);
        return ResponseEntity.ok(folder);
    }

    @GetMapping("children-folders/{parentId}/children")
    public List<Folder> listChildren(@PathVariable Long parentId) {
        return folderService.listChildren(parentId);
    }

    @GetMapping("/root-folders")
    public List<Folder> listRootFolders() {
        return folderService.listChildren(null);
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<Optional<Folder>> getFolderById(@PathVariable Long folderId) {
        return ResponseEntity.ok(folderService.findFolder(folderId));
    }

    @PutMapping("/folders/{id}")
    public ResponseEntity<Folder> updateFolder(
            @PathVariable Long id,
            @RequestBody FolderUpdateDTO dto) {

        Folder updated = folderService.updateFolder(
                id,
                dto.getName(),
                dto.getCourse()
        );

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/folders/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {

        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-course")
    public ResponseEntity<List<Folder>> getFoldersByCourse(@RequestParam String course) {
        List<Folder> folders = folderService.findFoldersByCourse(course);
        return ResponseEntity.ok(folders);
    }


}
