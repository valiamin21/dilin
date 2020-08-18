package ir.proglovving.dilin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import ir.proglovving.dilin.data_model.NotebookWord;

public class ExportDatabaseCSVTask extends AsyncTask<String, Integer, Boolean> {
    private static final String TAG = "ExportDatabaseCSVTask";
    private ProgressDialog dialog;
    private Context context;
    private List<NotebookWord> words;
    private String csvName;

    public ExportDatabaseCSVTask(Context context, List<NotebookWord> words, String csvName) {
        this.context = context;
        this.words = words;
        this.csvName = csvName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        dialog = new ProgressDialog(context);
        // TODO: 7/19/19 modify below message to persian message
        dialog.setTitle("exporting data title");
        dialog.setMessage("Exporting Database");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.show();

        Log.i(TAG, "onPreExecute: ");
    }

    @SuppressLint("WrongConstant")
    @Override
    protected Boolean doInBackground(String... strings) {
        String exportDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name);
        File exportDir = new File(exportDirPath);

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String csvFilePath = exportDirPath + "/" + csvName + ".csv";
        File csvFile = new File(csvFilePath);
        csvFile.delete();

        try {
            csvFile.createNewFile();
            csvFile.setWritable(true);

            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));

            //this code writes columns: word, meaning, bookmarked
            csvWriter.writeNext(
                    new String[]{
                            context.getString(R.string.word),
                            context.getString(R.string.meaning)
                    }
            );

            for (int i = 0; i < words.size(); i++) {
                NotebookWord word = words.get(i);
                String[] recordValues = new String[3];
                recordValues[0] = word.getWord();
                recordValues[1] = word.getMeaning();
//                if (word.isBookmark()) {
//                    recordValues[2] = context.getString(R.string.yes);
//                } else {
//                    recordValues[2] = "";
//                }
                csvWriter.writeNext(recordValues);
                publishProgress((i + 1) * 100 / words.size());
            }

            csvWriter.close();


            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: ", e);
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setProgressStyle(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (success) {
            Toast.makeText(context, context.getString(R.string.operation_done_successfully), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
        }
    }


    public class CSVWriter {

        private PrintWriter pw;

        private char separator;

        private char quotechar;

        private char escapechar;

        private String lineEnd;

        /**
         * The character used for escaping quotes.
         */
        public static final char DEFAULT_ESCAPE_CHARACTER = '"';

        /**
         * The default separator to use if none is supplied to the constructor.
         */
        public static final char DEFAULT_SEPARATOR = ',';

        /**
         * The default quote character to use if none is supplied to the
         * constructor.
         */
        public static final char DEFAULT_QUOTE_CHARACTER = '"';

        /**
         * The quote constant to use when you wish to suppress all quoting.
         */
        public static final char NO_QUOTE_CHARACTER = '\u0000';

        /**
         * The escape constant to use when you wish to suppress all escaping.
         */
        public static final char NO_ESCAPE_CHARACTER = '\u0000';

        /**
         * Default line terminator uses platform encoding.
         */
        public static final String DEFAULT_LINE_END = "\n";

        /**
         * Constructs CSVWriter using a comma for the separator.
         *
         * @param writer the writer to an underlying CSV source.
         */
        public CSVWriter(Writer writer) {
            this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                    DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        }

        /**
         * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
         *
         * @param writer     the writer to an underlying CSV source.
         * @param separator  the delimiter to use for separating entries
         * @param quotechar  the character to use for quoted elements
         * @param escapechar the character to use for escaping quotechars or escapechars
         * @param lineEnd    the line feed terminator to use
         */
        public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
            this.pw = new PrintWriter(writer);
            this.separator = separator;
            this.quotechar = quotechar;
            this.escapechar = escapechar;
            this.lineEnd = lineEnd;
        }

        /**
         * Writes the next line to the file.
         *
         * @param nextLine a string array with each comma-separated element as a separate
         *                 entry.
         */
        public void writeNext(String[] nextLine) {

            if (nextLine == null)
                return;

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nextLine.length; i++) {

                if (i != 0) {
                    sb.append(separator);
                }

                String nextElement = nextLine[i];
                if (nextElement == null)
                    continue;
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
                for (int j = 0; j < nextElement.length(); j++) {
                    char nextChar = nextElement.charAt(j);
                    if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                        sb.append(escapechar).append(nextChar);
                    } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                        sb.append(escapechar).append(nextChar);
                    } else {
                        sb.append(nextChar);
                    }
                }
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
            }

            sb.append(lineEnd);
            pw.write(sb.toString());

        }

        /**
         * Flush underlying stream to writer.
         *
         * @throws IOException if bad things happen
         */
        public void flush() throws IOException {

            pw.flush();

        }

        /**
         * Close the underlying stream writer flushing any buffered content.
         *
         * @throws IOException if bad things happen
         */
        public void close() throws IOException {
            pw.flush();
            pw.close();
        }

    }
}