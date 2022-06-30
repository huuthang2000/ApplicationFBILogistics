package com.example.demoapp.view.fragment.dom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demoapp.R;
import com.example.demoapp.adapter.ImportDomAdapter;
import com.example.demoapp.databinding.FragmentDomImportBinding;
import com.example.demoapp.model.DomImport;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.dialog.dom.dom_import.DialogDomImportInsert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DomImportFragment extends Fragment implements View.OnClickListener {

    private FragmentDomImportBinding binding;
    private ImportDomAdapter mImportDomAdapter;
    private SearchView searchView;

    private List<DomImport> mDomImportList = new ArrayList<>();

    private String month = "";
    private String continent = "";
    private String radioItem = "All";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDomImportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mImportDomAdapter = new ImportDomAdapter(getContext());

        setHasOptionsMenu(true);
        getAllData();
        setAutoComplete();
        setButtons();

        return view;
    }

    public void setUpRecyclerView(String m, String c, String r) {
        if (!m.isEmpty() && !c.isEmpty()) {
            mImportDomAdapter.setDomImport(filterData(m, c, r));
            binding.rcvDomImport.setAdapter(mImportDomAdapter);
            binding.rcvDomImport.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public List<DomImport> filterData(String m, String c, String r) {
        List<DomImport> subList = new ArrayList<>();
        try {
            for (DomImport domImport : mDomImportList) {
                if (r.equalsIgnoreCase("all")) {
                    if (domImport.getMonth().equalsIgnoreCase(m) && domImport.getContinent().equalsIgnoreCase(c)) {
                        subList.add(domImport);
                    }
                } else {
                    if (domImport.getMonth().equalsIgnoreCase(m) && domImport.getContinent().equalsIgnoreCase(c)
                            && domImport.getType().equalsIgnoreCase(r)) {
                        subList.add(domImport);
                    }
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public List<DomImport> filterDataResume(String m, String c, String r, List<DomImport> list) {
        List<DomImport> subList = new ArrayList<>();
        try {
            for (DomImport domImport : list) {
                if (r.equalsIgnoreCase("all")) {
                    if (domImport.getMonth().equalsIgnoreCase(m) && domImport.getContinent().equalsIgnoreCase(c)) {
                        subList.add(domImport);
                    }
                } else {
                    if (domImport.getMonth().equalsIgnoreCase(m) && domImport.getContinent().equalsIgnoreCase(c)
                            && domImport.getType().equalsIgnoreCase(r)) {
                        subList.add(domImport);
                    }
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public void setAutoComplete() {
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.autoDomMonth.setAdapter(adapterItemsMonth);
        binding.autoDomContinent.setAdapter(adapterItemsContinent);

        binding.autoDomMonth.setOnItemClickListener((adapterView, view, i, l) -> {
            month = adapterView.getItemAtPosition(i).toString();
            setUpRecyclerView(month, continent, radioItem);
        });

        binding.autoDomContinent.setOnItemClickListener((adapterView, view, i, l) -> {
            continent = adapterView.getItemAtPosition(i).toString();
            setUpRecyclerView(month, continent, radioItem);
        });
    }

    public void getAllData() {
        try {
            this.mDomImportList = new ArrayList<>();

            // get current user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // get path of database name "Users" cotaining users info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Import");
            // get all data from path
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mDomImportList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DomImport domImport = ds.getValue(DomImport.class);
                        // get all users except currently signed is user
                        mDomImportList.add(domImport);
                    }
                    sortDomImport(mDomImportList);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (NullPointerException nullPointerException){
            Toast.makeText(getContext(), nullPointerException.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public List<DomImport> sortDomImport(List<DomImport> list){
        List<DomImport> result = new ArrayList<>();
        for(int i =list.size()-1; i>=0; i--){
            result.add(list.get(i));
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();


        binding.rcvDomImport.setAdapter(mImportDomAdapter);
    }

    public void setButtons() {
        binding.domImportFab.setOnClickListener(this);

        binding.radioImportAll.setOnClickListener(this);
        binding.radioImportAll.performClick();

        binding.radioImportFr.setOnClickListener(this);

        binding.radioImportRf.setOnClickListener(this);

        binding.radioImportOt.setOnClickListener(this);

        binding.radioImportIso.setOnClickListener(this);

        binding.radioImportFt.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.dom_import_fab:
                DialogFragment dialogFragment = DialogDomImportInsert.getInstance();
                dialogFragment.show(getChildFragmentManager(), "ImportDom");

            case R.id.radio_import_all:
                radioItem = binding.radioImportAll.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_import_ft:
                radioItem = binding.radioImportFt.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_import_rf:
                radioItem = binding.radioImportRf.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_import_ot:
                radioItem = binding.radioImportOt.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_import_fr:
                radioItem = binding.radioImportFr.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_import_iso:
                radioItem = binding.radioImportIso.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                priceListAirImportAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                priceListAirImportAdapter.setDataAir(prepareDataForRecyclerView(month,continent));
                filter(s);
                return false;
            }
        });

    }
    private void filter(String text){
        List<DomImport> filteredList = new ArrayList<>();
        for( DomImport domImport: filterData(month, continent, radioItem)){
            if(domImport.getPortReceive().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(domImport);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        }else {
            mImportDomAdapter.filterList(filteredList);
        }
    }
}