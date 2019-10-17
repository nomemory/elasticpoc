package net.andreinc.elasticpoc.query.controller;

import net.andreinc.elasticpoc.query.controller.dto.VisitedReq;
import net.andreinc.elasticpoc.query.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static net.andreinc.aleph.AlephFormatter.str;

@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @PostMapping("/query/visited")
    public ResponseEntity visited(@RequestBody VisitedReq visitedReq) throws IOException {

        String name = visitedReq.getName();
        String city = visitedReq.getCity();

        System.out.println(str("name = #{name} ; city = #{city}").args("name", name, "city", city).fmt());

        return queryService.visited(name, city) ?
                        new ResponseEntity(HttpStatus.OK) :
                        new ResponseEntity(HttpStatus.NOT_FOUND);
    }
 }
