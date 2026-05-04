# Назначение

 - Расширение для Java HTTP;
 - Позволяет выполнять HTTP запросы описанные в виде текста;

# Пример

## Выполнение GET-запроса HTTP-клиентом Java
```java
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://example.com/"))
                    .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertTrue(response.statusCode() == 200);
```

## Выполнение GET-запроса JURL
```java
    ResponseMessage response = jurl("http://example.com/").fetch();

    assertTrue(response.getStatus().isOk());
```

# Фичи

 - [Параметризация запроса](./docs/parameterize-request.md)
 - [Маппинг результата](./docs/response-extractor.md)
 - [Обмен несколькими сообщениями - HTTP диалог](./docs/http-conversation.md)
 - [Фильтрация](./docs/exchange-filters.md)
 - [Поддержка multipart сообщений](./docs/multipart-messages.md)