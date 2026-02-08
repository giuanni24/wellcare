package maindir.model;


import maindir.model.enums.Role;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String password;
    protected Role role;
    protected String email;
    protected String fiscalCode;
    protected String name;
    protected String surname;


    public User() {
    }

    public User(String email, String password, String name,String surname, String fiscalCode,  Role role) {
        this.surname = surname;
        this.name = name;
        this.fiscalCode = fiscalCode;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public User(String email, Role role) {
        this.email = email;
        this.role = role;
    }
    public User(Long id, Role role,
                String email, String fiscalCode, String name, String surname) {
        this.id = id;
        this.role = role;
        this.email = email;
        this.fiscalCode = fiscalCode;
        this.name = name;
        this.surname = surname;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}

