package com.samv;

public class Mine {
    private int x;
    private int y;
    private int z;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }


    public Mine(int x, int  y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Mine ))
            return false;
        Mine m = (Mine)o;
        return (m.x == this.x) && (m.y == this.y) && (m.z == this.z) ;
    }


    @Override
    public int hashCode() {
        return String.valueOf(this.x +this.y ).hashCode();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ(){
        return z;
    }
}
