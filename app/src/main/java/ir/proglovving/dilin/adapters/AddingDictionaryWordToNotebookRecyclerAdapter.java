package ir.proglovving.dilin.adapters;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.data_model.DictionaryWord;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;
import ir.proglovving.dilin.views.fragment.NotebookListFragment;

public class AddingDictionaryWordToNotebookRecyclerAdapter extends RecyclerView.Adapter<AddingDictionaryWordToNotebookRecyclerAdapter.AddingDictionaryWordToNotebookViewHolder> {

    private List<Notebook> notebookList;
    private Context context;
    private Word word;
    private Dialog dialog;

    public AddingDictionaryWordToNotebookRecyclerAdapter(Context context, DictionaryWord dictionaryWord, Dialog dialog) {
        this.notebookList = new NotebookOpenHelper(context).getNotebookList();
        this.context = context;

        word = new Word();
        word.setId(dictionaryWord.getId());
        word.setWord(dictionaryWord.getWord());
        word.setMeaning(dictionaryWord.getMeaning());

        this.dialog = dialog;
    }

    @NonNull
    @Override
    public AddingDictionaryWordToNotebookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adding_dictionary_word_to_notebook, viewGroup, false);

        return new AddingDictionaryWordToNotebookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddingDictionaryWordToNotebookViewHolder holder, int i) {
        holder.notebookNameTextView.setText(notebookList.get(i).getNoteBookName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WordsOpenHelper(context, notebookList.get(holder.getAdapterPosition()).getId()).addWord(word);
                Toast.makeText(context, "کلمه‌ی مورد نظر با موفقیت به " + notebookList.get(holder.getAdapterPosition()).getNoteBookName() + " اضافه شد!", Toast.LENGTH_SHORT).show();
                NotebookListFragment.updateMeByBroadcast(context);
                dialog.hide();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notebookList.size();
    }


    static class AddingDictionaryWordToNotebookViewHolder extends RecyclerView.ViewHolder {
        private TextView notebookNameTextView;

        public AddingDictionaryWordToNotebookViewHolder(@NonNull View itemView) {
            super(itemView);
            notebookNameTextView = itemView.findViewById(R.id.tv_notebook_name);

        }
    }
}
