package com.lgd.demo.entity;

/**
 * 成绩单
 */
public class Transcript {

    private String id;

    private String stu_id;

    //chinese grade
    private int chinese;

    //math grade
    private int math;

    //english grade
    private int english;

    //total grade
    private int total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public int getChinese() {
        return chinese;
    }

    public void setChinese(int chinese) {
        this.chinese = chinese;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getEnglish() {
        return english;
    }

    public void setEnglish(int english) {
        this.english = english;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Transcript{" +
                "id='" + id + '\'' +
                ", stu_id='" + stu_id + '\'' +
                ", chinese=" + chinese +
                ", math=" + math +
                ", english=" + english +
                ", total=" + total +
                '}';
    }
}
