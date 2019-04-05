package com.kurenchuksergey.diplom.config.channel;

public enum Channel {
    TASK_TO_MANAGER("taskToManager"), TASK_TO_WORKER("taskToWorker");
    private String channelName;
    Channel(String channelName){
        this.channelName = channelName;
    }
    @Override
    public String toString(){
        return channelName;
    }
}
