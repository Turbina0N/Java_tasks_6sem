
import Classes.Person;
import Classes.*;

import toJSON.ToString;

import java.util.*;


public class ToStringTest {
    public static void main(String[] args) throws IllegalAccessException {
        // Пример класса с различными полями, используя List<Object> для scores

        List<Double> scores = List.of(100.0, 95.5, 88.0);
        Map<String, String> address = Map.of("city", "New York", "zip", "10001");

        Person person = new Person("John Doe", 30, false, scores, address);

        String jsonOutput = ToString.toJson(person);
        System.out.println(jsonOutput);

    }

}