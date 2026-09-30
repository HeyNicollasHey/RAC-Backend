package br.edu.catolica.academicrepository.service;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final ElasticsearchOperations operations;

    public SearchService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public SearchHits<Map> searchAll(String text) {
        IndexCoordinates indexes = IndexCoordinates.of("fileindex", "folderindex");

        String json = """
{
  "query_string": {
    "query": "*%s*",
    "fields": ["*"],
    "analyze_wildcard": true,
    "default_operator": "AND"
  }
}
""".formatted(escapeForQueryString(text));

        StringQuery query = new StringQuery(json);

        return operations.search(query, Map.class, indexes);
    }

    private String escapeForQueryString(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace(":", "\\:")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("^", "\\^")
                .replace("~", "\\~")
                .replace("!", "\\!")
                .replace("/", "\\/")
                .trim();
    }

}


