package com.gwm.cloudecs.model.VO;

public class VolumeTotalListVO {
    //存储个数
    int  size;

    // 磁盘个数
    int volumeCount;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getVolumeCount() {
        return volumeCount;
    }

    public void setVolumeCount(int volumeCount) {
        this.volumeCount = volumeCount;
    }
}
