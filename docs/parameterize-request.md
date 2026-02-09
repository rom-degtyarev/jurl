### [<< Документация](../readme.md)

### Параметризация запроса

```java
    // параметризация запроса
    ResponseMessage response = jurl(conversation ->
            conversation.withParameters(
                    "host", "https://my-service.ru",
                    "param1", "AAA",
                    "param2", "BBB"
            ).withExchange(rq -> response("HTTP/1.1 200 OK")) // <- заглушить ответ
    ).andThen("""
            POST http://${host}/api?param1=${param1}&param2=${param2}
            Content-Type: application/json
            
            < classpath:test.json"""
    ).fetch();
    
    // обработка ответа
    assertTrue(response.getStatus().isOk());
```

### [<< Документация](../readme.md)