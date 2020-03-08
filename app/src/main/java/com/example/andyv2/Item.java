package com.example.andyv2;

public class Item {
    String itemId;
    String itemName;
    String itemPhotoURL;
    String voice;


    public Item() {}

    public Item(String itemId, String itemName,
                   String itemPhotoURL) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPhotoURL = itemPhotoURL;
        this.voice = voice;
    }

    public String getItemId() { return itemId; }
    public String getItemName() {return itemName;}
    public String getItemPhotoURL() {return itemPhotoURL;}
    public String getVoice() {return voice;}

    public void getItemId(String itemId) {
        this.itemId = itemId;
    }


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public void setItemPhotoURL(String itemPhotoURL) {
        this.itemPhotoURL = itemPhotoURL;
    }


    public void setVoice(String voice) {
        this.voice = voice;
    }
}
