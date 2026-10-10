package com.example.my_health_app.ui;

public class Global {
    public static Global instance;
    public int stepCount;
    public int distance;
    public int initialStepCount;// on stock le nombre initial de pas
    public float stepLength;
    public int stepCountTarget;
    private int speed;
    private long time;


    private Global() {
        stepCount=0;
        distance=0;
        initialStepCount=-1;
        stepLength = 0.762f;
        stepCountTarget = 10000;
        this.speed = 0;
        this.time = 0;
    }

    public static synchronized Global getInstance() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getInitialStepCount() {
        return initialStepCount;
    }

    public void setInitialStepCount(int initialStepCount) {
        this.initialStepCount = initialStepCount;
    }

    public float getStepLength() {
        return this.stepLength;
    }

    public int getStepCountTarget() {
        return this.stepCountTarget;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

