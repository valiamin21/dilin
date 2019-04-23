package com.example.amin.dictionande.data_model;

public class Voc {

    private int id;
    private String voc;
    private String meaning;
    private String pic_address;
    private String audio_address;
    private boolean isBookmark;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setVoc(String voc){
        this.voc = voc;
    }

    public String getVoc(){
        return voc;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPic_address() {
        return pic_address;
    }

    public void setPic_directory(String pic_address) {
        this.pic_address = pic_address;
    }

    public String getAudio_address() {
        return audio_address;
    }

    public void setAudio_directory(String audio_address) {
        this.audio_address = audio_address;
    }

    public boolean isBookmark() {
        return isBookmark;
    }

    public void setBookmark(boolean bookmark) {
        isBookmark = bookmark;
    }
}
