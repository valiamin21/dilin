package ir.proglovving.dilin.data_model;

public class DetailedWord extends Word {
    private int notebookId;
    public void setNotebookId(int notebookId){
        this.notebookId = notebookId;
    }

    public int getNotebookId(){
        return notebookId;
    }
}
