# Http. Simple Server

Этот проект представляет собой простую фреймворк для HTTP/1.1 протокола на базе Java 21.
Он предоставляет сервер на основе Selector, который позволяет обрабатывать одновременно несколько клиентских подключений. Данный фреймворк реализует все методы, поддерживаемые протоколом HTTP/1.1, и в случае ошибок возвращает соответствующие коды ошибок.
### Стек технологий
- Java 21
- JUnit 5
- Mockito
### Структура проекта
```
├── src/                                       # Каталог исходного кода
│   └── main.java.httpserver/     
│       ├── HttpHandler.java                   # Интерфейс обработчик
│       ├── HttpRequest.java                   # Реализация HTTP-запроса
│       ├── HttpResponse.java                  # Реализация HTTP-ответа
│       ├── HttpServer.java                    # Реализация сервера
│       ├── HttpServerApplication.java         # Точка входа в программу
├── test/                                      # Каталог тестового кода
│   └── main.java/      # Тесты сервера
│       └── HttpServerTest.java                       # Unit-тесты
├── pom.xml                                    # Файл конфигурации Maven-проекта
└── README.md                                  # Документация проекта
 ```

### Быстрый старт ###

Точка входа в приложение нахоится в классе HttpServerApplication
`
```
public class HttpServerApplication {
    public static void main(String[] args) {
        HttpServer server = new HttpServer("localhost", 8080);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```
Метод start() включает в себя создание обработчиков по умолчанию для всех запросов, они доступны по пути "/".

*Кастомные обработчики можно добавить с помощью метода addHandler().*

Пример:
```
addHandler("/", "GET", (req, res) -> {
    res.send(200, "Received GET request");
});
