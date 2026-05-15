package DTO;

import java.sql.Date;

public class Monitor {
    private int borrowId;
    private String borrowerName;
    private int itemId;          
    private String itemName;     
    private int quantity;
    private Date dateBorrowed;
    private Date deadline;       
    private Date dateReturned;
    private String receivedBy;
    private String status;       

    public Monitor(int borrowId, String borrowerName, int itemId, String itemName, int quantity, 
        Date dateBorrowed, Date deadline, Date dateReturned, String receivedBy, String status) {
        this.borrowId = borrowId;
        this.borrowerName = borrowerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.dateBorrowed = dateBorrowed;
        this.deadline = deadline;
        this.dateReturned = dateReturned;
        this.receivedBy = receivedBy;
        this.status = status;
    }

    public Monitor(String borrowerName, int itemId, int quantity, Date dateBorrowed, 
        Date deadline, String receivedBy) {
        this.borrowerName = borrowerName;
        this.itemId = itemId;
        this.quantity = quantity;
        this.dateBorrowed = dateBorrowed;
        this.deadline = deadline;
        this.receivedBy = receivedBy;
    }

    // Getters and Setters
    public int getBorrowId() { return borrowId; }
    
    public String getBorrowerName() { return borrowerName; }
    
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    
    public int getItemId() { return itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public Date getDateBorrowed() { return dateBorrowed; }
    public void setDateBorrowed(Date dateBorrowed) { this.dateBorrowed = dateBorrowed; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    
    public Date getDateReturned() { return dateReturned; }
    public void setDateReturned(Date dateReturned) { this.dateReturned = dateReturned; }
    
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
    
    public String getStatus() { return status; }
}