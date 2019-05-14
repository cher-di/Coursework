package com.dmitry.pickletax;

public class ScheduleItem {
    private Integer classroom_id;
    private Integer lesson_number;
    private Integer status;
    private String description;

    public Integer getClassroom_id() {
        return classroom_id;
    }

    public void setClassroom_id(Integer classroom_id) {
        this.classroom_id = classroom_id;
    }

    public Integer getLesson_number() {
        return lesson_number;
    }

    public void setLesson_number(Integer lesson_number) {
        this.lesson_number = lesson_number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ScheduleItem(Integer classroom_id, Integer lesson_number, Integer status, String description) {
        this.classroom_id = classroom_id;
        this.lesson_number = lesson_number;
        this.status = status;
        this.description = description;
    }


}
