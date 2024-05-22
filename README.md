# Java JSON Parser
Java JSON Parser - это библиотека для обработки JSON в Java без использования внешних библиотек. Эта библиотека позволяет конвертировать JSON строки в объекты Java, карты (Map<String, Object>), а также в пользовательские классы и обратно.
Реализация поддерживает следующие типы: примитивные типы, упакованные типы, null, массивы, классы, коллекции.

### Стек технологий
- Java 21

### Структура проекта
```
├── src/                             # Каталог исходного кода
│   └── main.java/     
│       ├── Classes/.java            # Каталог с классами для тестов
|           ├── Person                 # Класс Person для тестирования
│       ├── toJSON                   # Каталог для преобразования класса в формат Json
│           ├── ToString.java          # Класс для преобразования класса в формат Json
│       ├── toObject                   # Каталог для преобразования Json в объекты
│           ├── JsonMapper.java        # Класс для преобразования карты (Map<String, Object>) в объекты пользовательского класса (Class<T>)
│           ├── LexAnalysing.java      # Класс лексического анализатора (для распознавания лексем)
│           ├── Lexem.java             # Класс для представления лексем 
│           ├── Parser.java            # Точка входа в программу. Реализация основного парсера.
├── test/                            # Каталог тестового кода
│   └── java/                            
│       └── LexAnalysingTest.java      # Тесты для распознавания лексем
│       └── ParserTest.java            # Тесты для JSON парсера    
│       └── ToStringTest.java          # Тесты для преобразования объекта в JSON
├── pom.xml                          # Файл конфигурации Maven-проекта
└── README.md                        # Документация проекта
 ```

### Быстрый старт ###
Точка входа в приложение нахоится в классе Parser.
Первый шаг в использовании парсера - токенизация входной строки JSON. Это преобразует строку в список токенов, которые затем используются парсером для создания объектов Java:

```
String jsonInput = "{\"name\":\"John Doe\",\"age\":30,\"isStudent\":false,\"scores\":[100, 95.5, 88],\"address\":{\"city\":\"New York\",\"zip\":\"10001\"}}";
LexAnalysing lexer = new LexAnalysing(jsonInput);
List<Lexem> tokens = lexer.tokenize();
if (tokens.isEmpty()) {
    throw new IllegalStateException("Token list is empty. Check the lexer.");
}
```

- Преобразование JSON в карту (Map<String, Object>)
```
Parser parser = new Parser(tokens);
Map<String, Object> resultMap = parser.parse(Map.class);
```
- Преобразование JSON в конкретный класс
```
Parser parser = new Parser(tokens);
Person person = parser.parse(Person.class);
```
- Выборочный доступ к значению по ключу. Этот метод полезен, если вам нужно получить конкретное значение по ключу без необходимости преобразовывать весь JSON.
```
Integer age = parser.parseByKey("age", Integer.class);
List<Object> scores = parser.parseByKey("scores", List.class);
```

- Преобразование JSON в Java Object.
```
Object genericObject = parser.parse();
System.out.println("Generic Object Output: " + genericObject);
```

