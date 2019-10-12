package com.tigcal.samples.gitcommits;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tigcal.samples.gitcommits.model.CommitItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommitAdapter extends RecyclerView.Adapter<CommitAdapter.ViewHolder> {
    private List<CommitItem> commitItems = new ArrayList<>();
    private OnCommitClickListener listener;

    public CommitAdapter(OnCommitClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_commit, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        viewHolder.bind(index, listener);
    }

    @Override
    public int getItemCount() {
        return commitItems.size();
    }

    public void setCommitItems(Collection<CommitItem> commitItemsToAdd) {
        commitItems.addAll(commitItemsToAdd);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        ImageView avatarImage;
        TextView messageText;
        TextView dateLoginText;

        ViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            messageText = itemView.findViewById(R.id.messageText);
            dateLoginText = itemView.findViewById(R.id.dateLoginText);
        }

        private void bind(final int index, final OnCommitClickListener listener) {
            final CommitItem commitItem = commitItems.get(index);
            Context context = itemView.getContext();

            Glide.with(context).load(commitItem.getAvatarUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                    )
                    .into(avatarImage);
            messageText.setText(context.getString(R.string.label_message, commitItem.getMessage()));

            if (index % 2 == 1) {
                crossFadeText(commitItem.getDate(), commitItem.getLogin(), true);
            } else {
                dateLoginText.setText(commitItem.getDate() + " / " + commitItem.getLogin());
                dateLoginText.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommitClick(commitItem);
                    setSelectedItem(index);
                }
            });

            if (commitItem.isSelected()) {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_background_green));
            } else {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_background_white));
            }
        }

        private void crossFadeText(final String date, final String login, final boolean dateIn) {
            int duration = 500;
            int pause = 1000;

            dateLoginText.setVisibility(View.INVISIBLE);
            if (dateIn) {
                dateLoginText.setText(date);
            } else {
                dateLoginText.setText(login);
            }

            Animation in = new AlphaAnimation(0, 1);
            in.setInterpolator(new DecelerateInterpolator());
            in.setStartOffset(duration + pause);
            in.setDuration(duration);

            Animation out = new AlphaAnimation(1, 0);
            out.setInterpolator(new AccelerateInterpolator());
            out.setStartOffset(duration + pause);
            out.setDuration(duration);

            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(in);
            if (!dateIn) {
                animationSet.addAnimation(out);
            }
            animationSet.setRepeatCount(1);
            dateLoginText.setAnimation(animationSet);

            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dateLoginText.setVisibility(View.VISIBLE);
                    crossFadeText(date, login, !dateIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void setSelectedItem(int index) {
        for (int i = 0; i < commitItems.size(); i++) {
            commitItems.get(i).setSelected(i == index);
            notifyItemChanged(i);
        }
    }

    public void clearSelection() {
        for (int i = 0; i < commitItems.size(); i++) {
            commitItems.get(i).setSelected(false);
            notifyItemChanged(i);
        }
    }

    public interface OnCommitClickListener {
        void onCommitClick(CommitItem commitItem);
    }
}
