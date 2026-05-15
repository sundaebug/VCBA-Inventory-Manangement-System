package DTO;

import java.time.LocalDate;

public class User {
 
private int id;
private String username;
private String pass;
private String role;
private String fullname;   

    public User (String username, String pass, String role, String fullname) {
        this.username = username;
        this.pass = pass;
        this.role = role;
        this.fullname = fullname;
    }
 
    public User (int id, String username, String pass, String role, String fullname) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.role = role;
        this.fullname = fullname;
    }

        public void setId(int id) {
            this.id = id;
        }
        
        public void setUserName(String username) {
            this.username = username;
        }
        
        public void setPass(String pass) {
            this.pass = pass;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public void setFullName(String fullname) {
            this.fullname = fullname;
        }
        
         public int getId() {
            return id;
        }
         
        public String getUserName() {
            return username;
        }
        
        public String getPass() {
            return pass;
        }
         
        public String getRole() {
            return role;
        }
        
        public String getFullName() {
            return fullname;
        }
}         