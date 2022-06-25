package com.example.demoapp.view.fragment.sales.truck;

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
import com.example.demoapp.adapter.sale.PriceListDryDomAdapter;
import com.example.demoapp.databinding.FragmentTruckDryBinding;
import com.example.demoapp.model.DomCold;
import com.example.demoapp.model.DomDry;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.viewmodel.CommunicateViewModel;
import com.example.demoapp.viewmodel.DomDryViewModel;

import java.util.ArrayList;
import java.util.List;


public class TruckDryFragment extends Fragment {

    private DomDryViewModel mDomDryViewModel;
    private PriceListDryDomAdapter mDryDomAdapter;
    private SearchView searchView;

    private List<DomDry> mDomDryList = new ArrayList<>();

    private String month = "";
    private String continent = "";

    private FragmentTruckDryBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTruckDryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mDryDomAdapter = new PriceListDryDomAdapter(getContext());
        mDomDryViewModel = new ViewModelProvider(this).get(DomDryViewModel.class);

        CommunicateViewModel mCommunicateViewModel = new ViewModelProvider(requireActivity()).get(CommunicateViewModel.class);

        mCommunicateViewModel.needReloading.observe(getViewLifecycleOwner(), needLoading -> {
            if (needLoading) {
                onResume();
            }
        });

        setHasOptionsMenu(true);
        getAllData();
        setAutoComplete();
        return view;
    }

    public void setUpRecyclerView(String m, String c) {
        if (!m.isEmpty() && !c.isEmpty()) {
            mDryDomAdapter.setDomDry(filterData(m, c));
            binding.rcvDomDry.setAdapter(mDryDomAdapter);
            binding.rcvDomDry.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public List<DomDry> filterData(String m, String c) {
        List<DomDry> subList = new ArrayList<>();
        try {
            for (DomDry domDry : mDomDryList) {
                if (domDry.getMonth().equalsIgnoreCase(m) && domDry.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domDry);
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public List<DomDry> filterDataResume(String m, String c, List<DomDry> list) {
        List<DomDry> subList = new ArrayList<>();
        try {
            for (DomDry domDry : list) {
                if (domDry.getMonth().equalsIgnoreCase(m) && domDry.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domDry);
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
            setUpRecyclerView(month, continent);
        });

        binding.autoDomContinent.setOnItemClickListener((adapterView, view, i, l) -> {
            continent = adapterView.getItemAtPosition(i).toString();
            setUpRecyclerView(month, continent);
        });
    }

    public void getAllData() {
        this.mDomDryList = new ArrayList<>();

        mDomDryViewModel.getAllData().observe(getViewLifecycleOwner(), domDries -> this.mDomDryList = domDries);
    }

    @Override
    public void onResume() {
        super.onResume();

        mDomDryViewModel.getAllData().observe(getViewLifecycleOwner(), domDries -> mDryDomAdapter.setDomDry(filterDataResume(month, continent, domDries)));

        binding.rcvDomDry.setAdapter(mDryDomAdapter);
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
        List<DomDry> filteredList = new ArrayList<>();
        for (DomDry domDry : filterData(month, continent)) {
            if (domDry.getAddressReceive().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(domDry);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            mDryDomAdapter.filterList(filteredList);
        }
    }


}