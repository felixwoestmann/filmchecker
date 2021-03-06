package me.murks.filmchecker.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import me.murks.filmchecker.R;
import me.murks.filmchecker.model.StoreModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChooseStoreTypeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_store_type_fragment, container, false);
        ListView storeList = (ListView) rootView.findViewById(R.id.store_list);
        final List<StoreModel> stores = getWizard().getApp().getStores();
        String[] storeNames = new String[stores.size()];
        for (int i = 0; i < stores.size(); i++) {
            storeNames[i] = getResources().getString(stores.get(i).getStoreName());
        }
        final ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(getContext(), R.layout.store_list_item, R.id.store_list_item_text, storeNames);
        storeList.setAdapter(storeAdapter);

        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getWizard().setStoreModel(stores.get(i));
            }
        });

        return rootView;
    }

    private AddFilmWizardActivity getWizard() {
        return (AddFilmWizardActivity) getActivity();
    }
}
