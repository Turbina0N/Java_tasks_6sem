package Classes;

import java.util.List;
import java.util.Map;

public class Person {
    private String name;
    private int age;
    private boolean isStudent;
    private List<Double> scores;
    private Map<String, String> address;

    public Person() {

    }

    public Person(String name, int age, boolean isStudent, List<Double> scores, Map<String, String> address) {
        this.name = name;
        this.age = age;
        this.isStudent = isStudent;
        this.scores = scores;
        this.address = address;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public void setAddress(Map<String, String> address) {
        this.address = address;
    }
}