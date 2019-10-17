package net.andreinc.elasticpoc.store.controller;

import net.andreinc.elasticpoc.store.service.StoreService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@RestController
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping("/init/people")
    public ResponseEntity initPeople() throws IOException {
        storeService.initPeople();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/init/people/cities")
    public ResponseEntity initPeopleCities() throws IOException {
        storeService.initPeopleCities();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/insert/entry/{index}/{pipeline}")
    public ResponseEntity insertData(@PathVariable String index, @PathVariable String pipeline, @RequestBody String csvLine) throws IOException {
        storeService.insertLine(index, pipeline, csvLine);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/insert/bulk/{index}/{pipeline}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity insertBulk(@PathVariable String index,
                                     @PathVariable String pipeline,
                                     @RequestParam("file") MultipartFile file) throws IOException {

        Path uploadedFile = storeService.writeUploadedFile(file);
        storeService.insertBulk(index, pipeline, uploadedFile);

        return new ResponseEntity(HttpStatus.OK);
    }
}
