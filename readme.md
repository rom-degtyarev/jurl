# Назначение

 - Расширение для Java HTTP;
 - Позволяет выполнять HTTP запросы описанные в виде текста;

# Пример

```java
        ResponseMessage response = jurl("GET https://ya.ru/").fetch();

        assertTrue(response.getStatus().isOk());
```

# Фичи

 - [Параметризация запроса](./docs/parameterize-request.md)
 - [Маппинг результата](./docs/response-extractor.md)
 - [Обмен несколькими сообщениями - HTTP диалог](./docs/http-conversation.md)
 - [Фильтрация](./docs/exchange-filters.md)
 - [Поддержка multipart сообщений](./docs/multipart-messages.md)