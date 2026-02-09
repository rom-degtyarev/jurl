### [<< Документация](../readme.md)

### Обмен несколькими сообщениями - HTTP диалог

```java
    ResponseMessage response = jurl(conversation -> conversation
            .withParameters("yandex", "https://yandex.ru")
    ).andThen("GET ${yandex}/metro")
            .andThen("GET ${yandex}/images")
            .andThen("GET ${yandex}/maps")
            .fetch();

    assertTrue(response.getStatus().isSuccess() || response.getStatus().isRedirected());
```

### [<< Документация](../readme.md)