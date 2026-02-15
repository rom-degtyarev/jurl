### [<< Документация](../readme.md)

### Фильтрация

```java
    // создаём фильтр
    Filter<RequestMessage, ResponseMessage> timer = invocation -> {
        long start = System.currentTimeMillis();
        ResponseMessage response = invocation.invoke();
        long time = System.currentTimeMillis() - start;
        System.out.printf("HTTP exchange duration %s%n", duration(time));
        return response;
    };
    
    // настройка диалога
    Conversation conversation = jurl(options ->
            options
                    .withFilter(timer)
                    .withExchange(rq -> response("HTTP/1.1 200 OK")) // <- заглушка ответа
    );
    
    // выполняем запрос
    ResponseMessage response = conversation
            .andThen("GET http://my-service/api?aaa=bbb")
            .fetch();
    
    // обработка ответа
    assertTrue(response.getStatus().isOk());
```
**Результат**
```shell
HTTP exchange duration 00:00:00.001

Process finished with exit code 0
```

### [<< Документация](../readme.md)