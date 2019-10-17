package net.andreinc.elasticpoc.query.service;

import net.andreinc.aleph.AlephFormatter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.query.QuerySearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.andreinc.aleph.AlephFormatter.str;

@Service
public class QueryService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public boolean visited(String name, String city) throws IOException {
        Path jsonPath = ResourceUtils.getFile("classpath:./queries/visitedcity.json").toPath();
        String json = Files.readString(jsonPath, StandardCharsets.US_ASCII);
        String query = str(json)
                      .arg("name", name)
                      .arg("city", city)
                      .fmt();
        System.out.println(query);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.wrapperQuery(query));
        searchRequest.indices("person");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse.getHits().getHits().length != 0;
    }
}
