package br.edu.catolica.academicrepository.service;

import br.edu.catolica.academicrepository.entity.Folder;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class FolderSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public FolderSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Folder indexFolder(Folder folder) {
        return elasticsearchOperations.save(folder);
    }

    public SearchHits<Folder> searchByName(String name) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(name)))
                .build();

        return elasticsearchOperations.search(query, Folder.class);
    }

    public void deleteById(Long id) {
        elasticsearchOperations.delete(String.valueOf(id), Folder.class);
    }
}
