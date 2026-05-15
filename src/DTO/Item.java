package DTO;

import java.sql.Date;

public class Item {
 
private int id;
private String item_name;
private String category;
private int  quantity;
private double unit_price;
private String supplier;
private Date date_added;   

    public Item (String item_name, String category, int quantity, double unit_price, String supplier, Date date_added) {
        this.item_name = item_name;
        this.category = category;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.supplier = supplier;
        this.date_added = date_added;
    }
 
    public Item (int id, String item_name, String category, int quantity, double unit_price, String supplier, Date date_added) {
        this.id = id;
        this.item_name = item_name;
        this.category = category;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.supplier = supplier;
        this.date_added = date_added;
    }

        public void setId(int id) {
            this.id = id;
        }
        
        public void setItemName(String item_name) {
            this.item_name = item_name;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public void setUnitPrice(double unit_price) {
            this.unit_price = unit_price;
        }
        
        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
        
        public void setDateAdded(Date date_addded) { 
            this.date_added = date_added; 
}

        
         public int getId() {
            return id;
        }
         
        public String getItemName() {
            return item_name;
        }
        
        public String getCategory() {
            return category;
        }
         
        public int getQuantity() {
            return quantity;
        }
        
        public double getUnitPrice() {
            return unit_price;
        }
         
        public String getSupplier() {
            return supplier;
        }
        
        public Date getDateAdded() {
            return date_added;
        }
}         
        
