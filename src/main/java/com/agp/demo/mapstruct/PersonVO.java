package com.agp.demo.mapstruct;

public class PersonVO {
    private Integer id;
    private String userName;
    private Integer userAge;
    public PersonVO(){}
    public PersonVO(Integer id, String userName, Integer userAge) {
        this.id = id;
        this.userName = userName;
        this.userAge = userAge;
    }

    @Override
    public String toString() {
        return "PersonVO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userAge=" + userAge +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }
}
