package com.tigcal.samples.gitcommits.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tigcal.samples.gitcommits.R;
import com.tigcal.samples.gitcommits.model.CommitItem;

public class DetailsFragment extends Fragment {
    private CommitDetailsListener commitDetailsListener;

    private ImageView avatarImage;
    private ImageView closeImage;

    private TextView dateText;
    private TextView loginText;
    private TextView shaText;
    private TextView messageText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        dateText = view.findViewById(R.id.dateText);
        loginText = view.findViewById(R.id.loginText);
        shaText = view.findViewById(R.id.shaText);
        messageText = view.findViewById(R.id.messageText);

        avatarImage = view.findViewById(R.id.avatarImage);
        closeImage = view.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitDetailsListener.closeCommitDetails();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            commitDetailsListener = (CommitDetailsListener) activity;
        } catch (ClassCastException exception) {
            throw new ClassCastException(activity.toString() + " should implement CommitDetailsListener");
        }
    }

    public void showCommitDetails(final CommitItem commitItem) {
        Glide.with(getActivity()).load(commitItem.getAvatarUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                )
                .into(avatarImage);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(commitItem.getHtmlUrl())) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(commitItem.getHtmlUrl()));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_no_url), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dateText.setText(getString(R.string.label_date, commitItem.getDate()));
        loginText.setText(getString(R.string.label_login, commitItem.getLogin()));
        shaText.setText(getString(R.string.label_sha, commitItem.getSha()));
        messageText.setText(getString(R.string.label_message, commitItem.getMessage()));
    }

    public interface CommitDetailsListener {
        void closeCommitDetails();
    }

}
