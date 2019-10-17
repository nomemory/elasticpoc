# elasticpoc

## Run the POC

### Use the right elasticsearch image

Check if `docker.elastic.co/elasticsearch/elasticsearch 7.3.2` is available as docker image:

```
docker images
```

Expected output:

```
REPOSITORY                                      TAG                 IMAGE ID            CREATED             SIZE
docker.elastic.co/elasticsearch/elasticsearch   7.3.2               d7052f192d01        5 weeks ago         706MB
```

If not, pull the image

```
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.3.2
```

### Run "containerized" elasticsearch

Check in the source folder the script `docker.sh` or run the following command manually:

```
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.3.2
```

PS: Normally this should be included in the gradle build.

### Test if elasticsearch is running correctly on the machine

```
curl localhost:9200/
```

A similar output should appear:

```
{
  "name" : "3e6c4e78bb05",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "Lj73nRs8RSSorLDim9vNqQ",
  "version" : {
    "number" : "7.3.2",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "1c1faf1",
    "build_date" : "2019-09-06T14:40:30.409026Z",
    "build_snapshot" : false,
    "lucene_version" : "8.1.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

### Have some data generated

Change directory to the datagen/ module, and run the gradle task in the that folder:

```
gradle run
```

Two files `people.csv` and `people_cities.csv` will be generated.

### Open all the microservices

All the microservices can be opened with the associated gradle task (gradle can be run directly from `elasticpoc/`):

| Microservice | Gradle | Swagger URL |
| ------------ | ------ | ------- |
| Catalog | `gradle :catalog:bootRun`| http://localhost:8081/swagger-ui.html |
| Query | `gradle :query:bootRun` | http://localhost:8083/swagger-ui.html |
| Store | `gradle :store:bootRun` | http://localhost:8082/swagger-ui.html |

### Setting up a clean elasticsearch setup

To clear and recreate every indices, mappings and pipelines in elastic it's recommended to call the `/setup/` rest controller.

```
curl -X GET "http://localhost:8081/setup/" -H "accept: */*"
```

After succesfuly running this we have:
* Created the `person` mapping;
* Created the pipelines to load `person`(s) and `person-cities` associations.

### Loading the initial sets of data

To insert the data we generated at the previous step we can use the swagger interface:
http://localhost:8082/swagger-ui.html#/store-controller/insertBulkUsingPOST

Or we can `curl` to make the request:

```
curl -X POST "http://localhost:8082/insert/bulk/person/person-pipeline" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@people.csv;type=text/csv"
```

and 

```
curl -X POST "http://localhost:8082/insert/bulk/person/person-city-pipeline" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "file=@people_cities.csv;type=text/csv"
```

### Querying

Testing if a person visited a city:

