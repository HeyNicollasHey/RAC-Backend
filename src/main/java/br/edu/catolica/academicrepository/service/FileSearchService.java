package br.edu.catolica.academicrepository.service;

import br.edu.catolica.academicrepository.entity.FileMeta;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class FileSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public FileSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }
    public FileMeta indexFile(FileMeta fileMeta) {
        return elasticsearchOperations.save(fileMeta);
    }

    public SearchHits<FileMeta> searchByName(String name) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(name)))
                .build();

        return elasticsearchOperations.search(query, FileMeta.class);
    }

    public void deleteById(Long id) {
        elasticsearchOperations.delete(String.valueOf(id), FileMeta.class);
    }
}
