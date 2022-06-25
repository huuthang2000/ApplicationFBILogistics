package com.example.demoapp.view.fragment.sales.truck;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demoapp.R;
import com.example.demoapp.adapter.sale.PriceListExportDomAdapter;
import com.example.demoapp.databinding.FragmentContainerExportBinding;
import com.example.demoapp.model.DomExport;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.viewmodel.CommunicateViewModel;
import com.example.demoapp.viewmodel.DomExportViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ContainerExportFragment extends Fragment implements View.OnClickListener{

    private DomExportViewModel mDomExportViewModel;
    private PriceListExportDomAdapter mExportDomAdapter;

    private List<DomExport> mDomExportList = new ArrayList<>();

    private SearchView searchView;
    private String month = "";
    private String continent = "";
    private String radioItem = "All";

    private FragmentContainerExportBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContainerExportBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        mExportDomAdapter = new PriceListExportDomAdapter(getContext());
        mDomExportViewModel = new ViewModelProvider(this).get(DomExportViewModel.class);

        CommunicateViewModel mCommunicateViewModel = new ViewModelProvider(requireActivity()).get(CommunicateViewModel.class);

        mCommunicateViewModel.needReloading.observe(getViewLifecycleOwner(), needLoading -> {
            if (needLoading) {
                onResume();
            }
        });

        setHasOptionsMenu(true);
        getAllData();
        setAutoComplete();
        setButtons();

        return view;
    }

    public void setUpRecyclerView(String m, String c, String r) {
        if (!m.isEmpty() && !c.isEmpty()) {
            mExportDomAdapter.setDomExport(filterData(m, c, r));
            binding.rcvDomExport.setAdapter(mExportDomAdapter);
            binding.rcvDomExport.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public List<DomExport> filterData(String m, String c, String r) {
        List<DomExport> subList = new ArrayList<>();
        try {
            for (DomExport domExport : mDomExportList) {
                if (r.equalsIgnoreCase("all")) {
                    if (domExport.getMonth().equalsIgnoreCase(m) && domExport.getContinent().equalsIgnoreCase(c)) {
                        subList.add(domExport);
                    }
                } else {
                    if (domExport.getMonth().equalsIgnoreCase(m) && domExport.getContinent().equalsIgnoreCase(c)
                            && domExport.getType().equalsIgnoreCase(r)) {
                        subList.add(domExport);
                    }
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public List<DomExport> filterDataResume(String m, String c, String r, List<DomExport> list) {
        List<DomExport> subList = new ArrayList<>();
        try {
            for (DomExport domExport : list) {
                if (r.equalsIgnoreCase("all")) {
                    if (domExport.getMonth().equalsIgnoreCase(m) && domExport.getContinent().equalsIgnoreCase(c)) {
                        subList.add(domExport);
                    }
                } else {
                    if (domExport.getMonth().equalsIgnoreCase(m) && domExport.getContinent().equalsIgnoreCase(c)
                            && domExport.getType().equalsIgnoreCase(r)) {
                        subList.add(domExport);
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
            // get current user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // get path of database name "Users" cotaining users info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Export");
            // get all data from path
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mDomExportList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DomExport domExport = ds.getValue(DomExport.class);
                        // get all users except currently signed is user
                        mDomExportList.add(domExport);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NullPointerException nullPointerException){
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mDomExportViewModel.getAllData().observe(getViewLifecycleOwner(), domExports -> mExportDomAdapter.setDomExport(filterDataResume(month, continent, radioItem, domExports)));

        binding.rcvDomExport.setAdapter(mExportDomAdapter);
    }

    public void setButtons() {

        binding.radioExportAll.setOnClickListener(this);
        binding.radioExportAll.performClick();

        binding.radioExportFr.setOnClickListener(this);

        binding.radioExportRf.setOnClickListener(this);

        binding.radioExportOt.setOnClickListener(this);

        binding.radioExportIso.setOnClickListener(this);

        binding.radioExportFt.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.radio_export_all:
                radioItem = binding.radioExportAll.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_export_ft:
                radioItem = binding.radioExportFt.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_export_rf:
                radioItem = binding.radioExportRf.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_export_ot:
                radioItem = binding.radioExportOt.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_export_fr:
                radioItem = binding.radioExportFr.getText().toString();
                setUpRecyclerView(month, continent, radioItem);
                break;

            case R.id.radio_export_iso:
                radioItem = binding.radioExportIso.getText().toString();
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

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

    }
    private void filter(String text) {
        List<DomExport> filteredList = new ArrayList<>();
        for (DomExport domExport : filterData(month, continent, radioItem)) {
            if (domExport.getPortExport().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(domExport);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            mExportDomAdapter.filterList(filteredList);
        }
    }

    }