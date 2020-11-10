package com.gwm.cloudecs.model.VO;

public class InstanceTotalListVO {

    //云主机台数汇总
    int  vcpus;
    //内存汇总
    int  memory;
    //cup 个数
    int  instanceConut;

    public int getVcpus() {
        return vcpus;
    }

    public void setVcpus(int vcpus) {
        this.vcpus = vcpus;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getInstanceConut() {
        return instanceConut;
    }

    public void setInstanceConut(int instanceConut) {
        this.instanceConut = instanceConut;
    }
}
