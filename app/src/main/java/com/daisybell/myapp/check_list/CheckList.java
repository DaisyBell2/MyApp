package com.daisybell.myapp.check_list;

import java.io.Serializable;

public class CheckList implements Serializable {
    public String nameCheckList, beforeAfter, par1, par2, par3, par4, par5;
    public int id;
    public int position;

    public CheckList() {
    }

    public CheckList(int id, String nameCheckList, String beforeAfter, String par1, String par2, String par3, String par4, String par5) {
        this.id = id;
        this.nameCheckList = nameCheckList;
        this.beforeAfter = beforeAfter;
        this.par1 = par1;
        this.par2 = par2;
        this.par3 = par3;
        this.par4 = par4;
        this.par5 = par5;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNameCheckList() {
        return nameCheckList;
    }

    public void setNameCheckList(String nameCheckList) {
        this.nameCheckList = nameCheckList;
    }

    public String getBeforeAfter() {
        return beforeAfter;
    }

    public void setBeforeAfter(String beforeAfter) {
        this.beforeAfter = beforeAfter;
    }

    public String getPar1() {
        return par1;
    }

    public void setPar1(String par1) {
        this.par1 = par1;
    }

    public String getPar2() {
        return par2;
    }

    public void setPar2(String par2) {
        this.par2 = par2;
    }

    public String getPar3() {
        return par3;
    }

    public void setPar3(String par3) {
        this.par3 = par3;
    }

    public String getPar4() {
        return par4;
    }

    public void setPar4(String par4) {
        this.par4 = par4;
    }

    public String getPar5() {
        return par5;
    }

    public void setPar5(String par5) {
        this.par5 = par5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
