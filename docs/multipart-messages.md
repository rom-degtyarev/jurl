### [<< Документация](../readme.md)

### Поддержка multipart сообщений

> **Отправка файла**

```java
    // given
    byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));
    
    // when
    ResponseMessage response = jurl(conversation -> conversation
            .withParameter("binary-content", toHexString(content))
            .withExchange(rq -> response("HTTP/1.1 200 OK"))
    ).andThen("""
            POST http://my-service/api
            content-type: multipart/form-data; boundary=123
            
            --123
            Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
            Content-Type: application/pdf
            
            ${binary-content}
            --123--"""
    ).fetch();
    
    // then
    assertTrue(response.getStatus().isOk());
```

> **Получение файла**

```java
    // given
    byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));
    
    ResponseMessage response = response(
            string("""
                    HTTP/1.1 200 OK
                    content-type: multipart/form-data; boundary=123
                    
                    --123
                    Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                    Content-Type: application/pdf
                    
                    ${binary-content}
                    --123--
                    """
            ).merge(parameter("binary-content", toHexString(content)))
    );
    
    // when
    ResponseMessage reply = jurl(conversation -> conversation
            .withExchange(rq -> response)
    ).andThen("GET http://my-service/api")
            .fetch();
    
    // then
    assertTrue(reply.getStatus().isOk());
    MultipartContent.BodyPart bodyPart = reply.getBody().getMultipartContent().parts().get(0);
    assertEquals(toHexString(content), toHexString(bodyPart.content()));
```

### [<< Документация](../readme.md)