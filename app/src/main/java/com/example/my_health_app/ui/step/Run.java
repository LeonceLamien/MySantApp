package com.example.my_health_app.ui.step;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Run{
    public String phone_id;
    public int step_number;
    public int distance;
    public boolean step_goal;
    public int average_speed;
    public LocalDateTime date;
    public long time;

    public Run(String phone_id, int step_number, int distance, boolean step_goal, int average_speed, LocalDateTime date, long time) {
        this.phone_id = phone_id;
        this.step_number = step_number;
        this.distance = distance;
        this.step_goal = step_goal;
        this.average_speed = average_speed;
        this.date = date;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Run run = (Run) o;
        return phone_id == run.phone_id && step_number == run.step_number && distance == run.distance && step_goal == run.step_goal && Objects.equals(average_speed, run.average_speed) && Objects.equals(date, run.date) && Objects.equals(time, run.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone_id, step_number, distance, step_goal, average_speed, date, time);
    }

    public String getPhone_id() {
        return phone_id;
    }

    public int getStep_number() {
        return step_number;
    }


    public int getDistance() {
        return distance;
    }


    public boolean isStep_goal() {
        return step_goal;
    }


    public int getAverage_speed() {
        return average_speed;
    }


    public LocalDateTime getDate() {
        return date;
    }


    public long getTime() {
        return time;
    }

}
