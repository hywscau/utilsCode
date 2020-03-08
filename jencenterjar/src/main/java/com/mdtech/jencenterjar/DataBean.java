package com.mdtech.jencenterjar;

/**
 * created by HYW on 2020/3/7 0007
 * Describe:
 */
public class DataBean {

    public String name;

    public String school;

    public int age;

    private DataTestBean dataTestBean;

    public DataTestBean getDataTestBean() {
        return dataTestBean;
    }

    public void setDataTestBean(DataTestBean dataTestBean) {
        this.dataTestBean = dataTestBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "name='" + name + '\'' +
                ", school='" + school + '\'' +
                ", age=" + age +
                '}';
    }
}
