package controller;

import java.util.concurrent.LinkedBlockingQueue;

public class Tasks {
    private static Tasks INSTANCE;
    private LinkedBlockingQueue<Object> incomingTasks = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Object> outgoingTasks = new LinkedBlockingQueue<>();
    private Tasks(){}
    public static Tasks getINSTANCE(){
        if (INSTANCE==null)
            INSTANCE=new Tasks();
        return INSTANCE;
    }
    public LinkedBlockingQueue getIncomingTasks(){
        return incomingTasks;
    }
    public LinkedBlockingQueue getOutgoingTasks(){
        return outgoingTasks;
    }
}
