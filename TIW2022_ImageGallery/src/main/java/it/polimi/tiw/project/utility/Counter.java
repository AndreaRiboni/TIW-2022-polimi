package it.polimi.tiw.project.utility;

public class Counter {
    private int value;

    public Counter(int start_value){
        value = start_value;
    }

    public int increment(){
        value++;
        return value;
    }

    public int get(){
        return value;
    }
}
