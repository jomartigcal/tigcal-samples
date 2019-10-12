package com.tigcal.samples.gitcommits.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tigcal.samples.gitcommits.CommitAdapter;
import com.tigcal.samples.gitcommits.R;
import com.tigcal.samples.gitcommits.model.CommitItem;
import com.tigcal.samples.gitcommits.util.NetworkUtil;

import org.json.JSONArray;

import java.util.List;

public class ListFragment extends Fragment {
    private static final String TAG = ListFragment.class.getSimpleName();
    private static final String GITHUB_USER = "material-components";
    private static final String GITHUB_REPO = "material-components-android";
    private Gson gson;

    private RecyclerView commitsRecyclerView;
    private CommitItemListener commitItemListener;
    private CommitAdapter commitAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_list, container, false);

        commitAdapter = new CommitAdapter(new CommitAdapter.OnCommitClickListener() {
            @Override
            public void onCommitClick(CommitItem commitItem) {
                commitItemListener.showCommitDetails(commitItem);
            }
        });

        commitsRecyclerView = view.findViewById(R.id.commits_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commitsRecyclerView.setLayoutManager(layoutManager);
        commitsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        commitsRecyclerView.setAdapter(commitAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = new Gson();
        getCommits(GITHUB_USER, GITHUB_REPO);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            commitItemListener = (CommitItemListener) activity;
        } catch (ClassCastException exception) {
            throw new ClassCastException(activity.toString() + " should implement CommitItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        commitItemListener = null;
    }

    private void getCommits(String githubUser, String githubRepo) {
        Log.d(TAG, NetworkUtil.getCommitsUrl(githubUser, githubRepo));
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, NetworkUtil.getCommitsUrl(githubUser, githubRepo), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response == null) {
                    Log.e(TAG, "response is null");
                    displayErrorMessage();
                } else {
                    displayCommits(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "VolleyError: " + error.getLocalizedMessage());
                displayErrorMessage();
            }
        });

        NetworkUtil.addToRequestQueue(getActivity(), request);
    }

    private void displayErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.error_getting_commits))
                .setPositiveButton(getString(R.string.button_ok), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void displayCommits(JSONArray response) {
        List<CommitItem> commitItems = gson.fromJson(response.toString(), new TypeToken<List<CommitItem>>(){}.getType());
        commitAdapter.setCommitItems(commitItems);
    }

    public void clearSelection() {
        commitAdapter.clearSelection();
    }

    public interface CommitItemListener {
        void showCommitDetails(CommitItem commitItem);
    }
}
