/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationproject;

import java.time.LocalDate;

/**
 *
 * @author ksyus
 */
public class Company {
    private String name;
    private String phone;
    private String email;
    private String website;
    private String type;
    private LocalDate date;
    private String responsible;
    private String comment;
    private DealStatus dealStatus = DealStatus.ZERO; // по умолчанию


    public Company(String name, String phone, String email, String website, String type, LocalDate date, String responsible) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.type = type;
        this.date = date;
        this.responsible = responsible;
        this.comment = "";
        
    }

    // Геттеры
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getActivityType() { return type; }
    public String getResponsible() { return responsible; }
    public String getComment() { return comment; }

    // Сеттеры 
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setWebsite(String website) { this.website = website; }
    public void setActivityType(String type) { this.type = type; }
    public void setResp(String user) { this.responsible = responsible; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // метод для отображения даты 
    public String getFormattedDate() {
        if (date == null) return "";
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    
    public DealStatus getDealStatus() { return dealStatus; }
    public void setDealStatus(DealStatus dealStatus) { this.dealStatus = dealStatus; }
    public boolean isInDeal() { return dealStatus != DealStatus.ZERO && dealStatus != DealStatus.COMPLETED ; }

}
