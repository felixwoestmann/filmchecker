package me.murks.filmchecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import me.murks.filmchecker.activities.FilmStatusListAdapter;
import me.murks.filmchecker.io.RossmanStatusProvider;
import me.murks.filmchecker.model.Film;
import me.murks.filmchecker.model.FilmStatus;

/**
 * Created by mark on 28.05.16.
 */
public class AsyncListViewLoader extends AsyncTask<String, Void, List<Pair<Film, FilmStatus>>> {
    private final ProgressDialog dialog;
    private final FilmStatusListAdapter adapter;
    private final Collection<Film> films;

    public AsyncListViewLoader(Context context, FilmStatusListAdapter adapter, Collection<Film> films) {
        dialog = new ProgressDialog(context);
        this.adapter = adapter;
        this.films = films;
    }

    @Override
    protected void onPostExecute(List<Pair<Film, FilmStatus>> result) {
        super.onPostExecute(result);
        adapter.addAll(result);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(dialog.getContext().getResources().getString(R.string.film_list_updateing_list));
        dialog.show();
    }

    @Override
    protected List<Pair<Film, FilmStatus>> doInBackground(String... params) {
        RossmanStatusProvider statusProvider = new RossmanStatusProvider();
        List<Pair<Film, FilmStatus>> results = new LinkedList<>();
        for (Film f: films) {
            try {
                FilmStatus status = statusProvider.getStatus(f);
                results.add(new Pair<>(f, status));
            } catch (IOException ioe) {
                results.add(new Pair<>(f, new FilmStatus(ioe.getLocalizedMessage())));
            }
        }
        return results;
    }
}