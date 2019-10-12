package com.tigcal.samples.gitcommits;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tigcal.samples.gitcommits.fragment.DetailsFragment;
import com.tigcal.samples.gitcommits.fragment.ListFragment;
import com.tigcal.samples.gitcommits.model.CommitItem;

public class MainActivity extends AppCompatActivity
        implements ListFragment.CommitItemListener, DetailsFragment.CommitDetailsListener {

    private ListFragment listFragment;
    private DetailsFragment detailsFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        listFragment = (ListFragment) fragmentManager.findFragmentById(R.id.list_fragment);

        detailsFragment = (DetailsFragment) fragmentManager.findFragmentById(R.id.details_fragment);
        if (detailsFragment != null && detailsFragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(detailsFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showCommitDetails(CommitItem commitItem) {
        if (detailsFragment != null && detailsFragment.isHidden()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.show(detailsFragment);
            fragmentTransaction.commit();
        }
        detailsFragment.showCommitDetails(commitItem);
    }

    @Override
    public void closeCommitDetails() {
        if (detailsFragment != null && !detailsFragment.isHidden()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(detailsFragment);
            fragmentTransaction.commit();
        }
        if (listFragment != null) {
            listFragment.clearSelection();
        }
    }
}
