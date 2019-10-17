package net.andreinc.elasticpoc.catalog.service;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.ingest.DeletePipelineRequest;
import org.elasticsearch.action.ingest.GetPipelineAction;
import org.elasticsearch.action.ingest.GetPipelineRequest;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CatalogService {

    private static Logger logger = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    private RestHighLevelClient rhlClient;

    private String[] indices = { "person", "car", "city" };
    private String[] pipelines = { "person-pipeline" , "person-city-pipeline" };

    public void deleteAllIndices() throws IOException {

        for(String index : indices) {
            GetIndexRequest getIndexRequest = new GetIndexRequest(index);
            if (rhlClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
                logger.info("Index {} found, deleting it.", index);
                DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
                rhlClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            }
        }
    }

    public void createAllIndices() throws IOException {
        for(String index : indices) {
            CreateIndexRequest request = new CreateIndexRequest(index);
            rhlClient.indices().create(request, RequestOptions.DEFAULT);
        }
    }

    public void createMapping(String index, String path) throws IOException {
        Path jsonPath = ResourceUtils.getFile("classpath:./mappings/"+path).toPath();
        String json = Files.readString(jsonPath, StandardCharsets.US_ASCII);
        PutMappingRequest putMappingRequest = new PutMappingRequest(index)
                .source(json, XContentType.JSON);
        rhlClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
    }

    public void createPipeline(String source, String name) throws IOException {
        Path jsonPath = ResourceUtils.getFile("classpath:./pipelines/"+source).toPath();
        String json = Files.readString(jsonPath, StandardCharsets.US_ASCII);
        PutPipelineRequest putPipelineRequest = new PutPipelineRequest(
                name,
                new BytesArray(json.getBytes(StandardCharsets.UTF_8)),
                XContentType.JSON
        );
        rhlClient.ingest().putPipeline(putPipelineRequest, RequestOptions.DEFAULT);
    }

    public void deletePipeline(String name) throws IOException {
        DeletePipelineRequest deletePipelineRequest = new DeletePipelineRequest(name);
        rhlClient.ingest().deletePipeline(deletePipelineRequest, RequestOptions.DEFAULT);
    }

    public void deletePipelines() throws IOException {
        for(String pipeline : pipelines) {
            GetPipelineRequest getPipelineRequest = new GetPipelineRequest(pipeline);
            if (rhlClient.ingest().getPipeline(getPipelineRequest, RequestOptions.DEFAULT).isFound()) {
                deletePipeline(pipeline);
            }
        }
    }

    public void createPipelines() throws IOException {
        createPipeline("person.json", "person-pipeline");
        createPipeline("person-city.json", "person-city-pipeline");
    }
}
