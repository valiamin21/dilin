package ir.proglovving.dilin.views.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.proglovving.dilin.R;

public class IntroFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE_DRAWABLE = "image_drawable";

    private String title;
    private String description;
    @DrawableRes
    int imageDrawable;

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(String title, String description, @DrawableRes int imageDrawable) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putInt(ARG_IMAGE_DRAWABLE, imageDrawable);
        fragment.setArguments(args);
        return fragment;
    }

    public static IntroFragment newInstance(Context context, @StringRes int title, @StringRes int description, @DrawableRes int imageDrawable) {
        return newInstance(context.getString(title),context.getString(description),imageDrawable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            imageDrawable = getArguments().getInt(ARG_IMAGE_DRAWABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_intro, container, false);
        TextView titleTextView = view.findViewById(R.id.intro_title);
        TextView descriptionTextView = view.findViewById(R.id.intro_description);
        ImageView imageView = view.findViewById(R.id.intro_image_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // deleting image outline for adjusting image radius defined in it's selector!
            imageView.setClipToOutline(true);
        }


        titleTextView.setText(title);
        descriptionTextView.setText(description);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), imageDrawable));
        return view;
    }
}
