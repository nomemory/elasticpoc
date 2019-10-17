package net.andreinc.elasticpoc.store.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.ingest.GetPipelineRequest;
import org.elasticsearch.action.ingest.GetPipelineResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static net.andreinc.aleph.AlephFormatter.str;
import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class StoreService {

    @Autowired
    private RestHighLevelClient rhlClient;

    public void initPeople() throws IOException {
        String peopleSource = "classpath:./initdata/people.csv";
        Path peopleCsvPath = ResourceUtils.getFile(peopleSource).toPath();
        Reader reader  = Files.newBufferedReader(peopleCsvPath);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        for (CSVRecord record : csvParser) {
            String id = record.get(0);
            String name = record.get(1);
            String email = record.get(2);
            String age = record.get(3);

            Map<String, Object> personMap = new HashMap<>();

            personMap.put("name", name);
            personMap.put("email", email);
            personMap.put("age", Integer.parseInt(age));
            personMap.put("visited", "visitor");

            IndexRequest indexRequest = new IndexRequest("person").id(id).source(personMap);
            rhlClient.index(indexRequest, DEFAULT);
        }
    }

    public void initPeopleCities() throws IOException {
        String peopleCitiesSource = "classpath:./initdata/people_cities.csv";
        Path peopleCitiesCsvPath = ResourceUtils.getFile(peopleCitiesSource).toPath();
        Reader reader  = Files.newBufferedReader(peopleCitiesCsvPath);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        for(CSVRecord record : csvParser) {
            String personId = record.get(0);
            String city = record.get(1);
            String date = record.get(2);

            Map<String, Object> personCityMap = new HashMap<>();

            personCityMap.put("city", city);
            personCityMap.put("date", date);

            Map<String, Object> visitMap = new HashMap<>();

            visitMap.put("name", "visit");
            visitMap.put("parent", personId);

            personCityMap.put("visited", visitMap);

            IndexRequest indexRequest = new IndexRequest("person")
                                                .id(UUID.randomUUID().toString())
                                                .routing(personId)
                                                .source(personCityMap);

            rhlClient.index(indexRequest, DEFAULT);
        }
    }

    public void insertLine(String index, String pipeline, String csvLine) throws IOException {
        if (isPipeline(pipeline)) {
            throw new IOException("Pipeline " + pipeline + "not found.");
        }

        // Read the ingest API metadata
        Path jsonPath = ResourceUtils.getFile("classpath:./meta/ingest.json").toPath();
        String source = str(Files.readString(jsonPath, StandardCharsets.US_ASCII))
                            .arg("csvLine", csvLine).fmt();

        IndexRequest indexRequest = new IndexRequest(index).setPipeline(pipeline).source(source, XContentType.JSON);
        rhlClient.index(indexRequest, DEFAULT);
    }

    //TODO if the file is very big introduce some form of "pagination"
    public void insertBulk(String index, String pipeline, Path csvFile) throws IOException {
        if (!isPipeline(pipeline)) {
            throw new IOException("Pipeline " + pipeline + " not found");
        }
        final BulkRequest bulkRequest = new BulkRequest();
        try(Stream<String> stream = Files.lines(csvFile)) {
            stream.forEach(line -> {
                Map<String, Object> value = new HashMap<>();
                value.put("tmp_csv_line", line);
                IndexRequest indexRequest = new IndexRequest(index).setPipeline(pipeline).source(value);
                bulkRequest.add(indexRequest);
            });
        }
        rhlClient.bulk(bulkRequest, DEFAULT);
    }

    protected boolean isPipeline(String pipeline) throws IOException {
        GetPipelineRequest getPipelineRequest = new GetPipelineRequest(pipeline);
        GetPipelineResponse getPipelineResponse = rhlClient.ingest().getPipeline(getPipelineRequest, DEFAULT);
        return getPipelineResponse.isFound();
    }

    public Path writeUploadedFile(MultipartFile multipartFile) throws IOException {
        String timeStamp = new Date().getTime() + "";
        File uploadedFile = new File(multipartFile.getOriginalFilename() + timeStamp);

        uploadedFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();

        return uploadedFile.toPath();
    }
}
