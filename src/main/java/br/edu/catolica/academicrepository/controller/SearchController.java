package br.edu.catolica.academicrepository.controller;

import br.edu.catolica.academicrepository.service.SearchService;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<Map<String,Object>> search(@RequestParam("q") String q) {
        SearchHits<Map> hits = searchService.searchAll(q);

        return hits.get().map(hit -> {
            Map<String, Object> src = hit.getContent();
            Map<String, Object> normalized = new LinkedHashMap<>();

            String index = hit.getIndex();

            normalized.put("id", src.get("id"));
            normalized.put("name", src.get("name"));

            if (index.equals("fileindex")) {
                normalized.put("type", "file");
                normalized.put("parentId", src.get("folderId"));
                normalized.put("downloadUrl", "/files/download/" + src.get("id"));
            } else {
                normalized.put("type", "folder");
                normalized.put("parentId", src.get("parentId"));
            }

            return normalized;
        }).toList();
    }


}
