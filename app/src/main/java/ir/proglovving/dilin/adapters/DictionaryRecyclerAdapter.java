package ir.proglovving.dilin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ir.proglovving.dilin.MyApplication;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.custom_views.ToolTip;
import ir.proglovving.dilin.data_model.DictionaryWord;

public class DictionaryRecyclerAdapter extends RecyclerView.Adapter<DictionaryRecyclerAdapter.DictionaryViewHolder> implements View.OnLongClickListener {

    private static final int VIEW_TYPE_FIRST_ITEM = 0, VIEW_TYPE_DEFAULT = 1;

    private final Context context;
    private final List<DictionaryWord> dictionaryWordList;
    private int marginTop;
    private RecyclerView recyclerView;

    public DictionaryRecyclerAdapter(Context context, List<DictionaryWord> dictionaryWordList, int marginTop) {
        this.context = context;
        this.dictionaryWordList = dictionaryWordList;
        this.marginTop = marginTop;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public DictionaryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FIRST_ITEM:
                View firstItemView = LayoutInflater.from(context).inflate(R.layout.item_dictionary, viewGroup, false);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) firstItemView.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, marginTop, layoutParams.rightMargin, layoutParams.bottomMargin);
                return new DictionaryViewHolder(firstItemView);

            case VIEW_TYPE_DEFAULT:
                return
                        new DictionaryViewHolder(
                                LayoutInflater.from(context).inflate(R.layout.item_dictionary, viewGroup, false)
                        );

            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final DictionaryViewHolder mViewHolder, int i) {
        final DictionaryWord dictionaryWord = dictionaryWordList.get(i);

        mViewHolder.wordTextView.setText(dictionaryWord.getWord());
        mViewHolder.meaningTextView.setText(dictionaryWord.getMeaning());

        mViewHolder.speechButtonUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.speechWord(dictionaryWord.getWord(), Locale.US, context);
            }
        });

        mViewHolder.speechButtonUK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.speechWord(dictionaryWord.getWord(), Locale.UK, context);
            }
        });


        mViewHolder.addToNotebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 12/20/19 complete this code
            }
        });

        // adding longClickListener for showing tooltips
        mViewHolder.addToNotebookButton.setOnLongClickListener(this);
        mViewHolder.speechButtonUK.setOnLongClickListener(this);
        mViewHolder.speechButtonUS.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return dictionaryWordList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FIRST_ITEM;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.img_add_to_notebook:
                ToolTip.show(context,context.getString(R.string.adding_to_notebook),v);
                break;
            case R.id.img_speech_us:
                ToolTip.show(context,context.getString(R.string.american_pronunciation),v);
                break;
            case R.id.img_speech_uk:
                ToolTip.show(context,context.getString(R.string.english_pronunciation),v);
                break;
        }
        return true;
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {
        private TextView wordTextView, meaningTextView;
        private ImageButton speechButtonUK, speechButtonUS, addToNotebookButton;

        public DictionaryViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_word);
            meaningTextView = itemView.findViewById(R.id.txt_meaning);
            speechButtonUK = itemView.findViewById(R.id.img_speech_uk);
            speechButtonUS = itemView.findViewById(R.id.img_speech_us);
            addToNotebookButton = itemView.findViewById(R.id.img_add_to_notebook);

        }
    }
}
