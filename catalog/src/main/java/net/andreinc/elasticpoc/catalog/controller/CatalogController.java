package net.andreinc.elasticpoc.catalog.controller;

import net.andreinc.elasticpoc.catalog.service.CatalogService;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CatalogController {

    private static Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/setup/indices")
    public ResponseEntity createIndices() throws IOException {
        catalogService.createAllIndices();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/setup/mappings")
    public ResponseEntity updateMappings() throws IOException {
        catalogService.createMapping("person", "person.json");
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/setup/indices")
    public ResponseEntity deleteIndices () throws IOException {
        catalogService.deleteAllIndices();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/setup/pipelines")
    public ResponseEntity createPipelines() throws IOException {
        catalogService.createPipeline("person.json", "person-pipeline");
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/pipelines")
    public ResponseEntity deletePipelines() throws IOException {
        catalogService.deletePipeline("person-pipeline");
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/setup/")
    public ResponseEntity setup() throws IOException {

        logger.info("Deleting existing indices.");
        catalogService.deleteAllIndices();

        logger.info("Creating new indices");
        catalogService.createAllIndices();

        logger.info("Creating mapping for {} from file {}", "person", "person.json");
        catalogService.createMapping("person", "person.json");

        logger.info("Deleting existing pipelines");
        catalogService.deletePipelines();

        logger.info("Creating new pipelines");
        catalogService.createPipelines();

        return new ResponseEntity(HttpStatus.OK);
    }
}
