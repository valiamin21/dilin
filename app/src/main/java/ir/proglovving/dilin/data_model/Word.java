package ir.proglovving.dilin.data_model;

public class Word {

    private int notebookId;
    private int id;
    private String word;
    private String meaning;
    private boolean isBookmark;

    public void setNotebookId(int notebookId){
        this.notebookId = notebookId;
    }

    public int getNotebookId(){
        return notebookId;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setWord(String word){
        this.word = word;
    }

    public String getWord(){
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isBookmark() {
        return isBookmark;
    }

    public void setBookmark(boolean bookmark) {
        isBookmark = bookmark;
    }
}
