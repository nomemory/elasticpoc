# elasticpoc

## Run the POC

### Use the right elasticsearch image

Check if `docker.elastic.co/elasticsearch/elasticsearch 7.3.2` is available as docker image:

```sh
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

  
