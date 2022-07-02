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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demoapp.R;
import com.example.demoapp.adapter.sale.PriceListColdDomAdapter;
import com.example.demoapp.databinding.FragmentTruckColdBinding;
import com.example.demoapp.model.DomCold;
import com.example.demoapp.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TruckColdFragment extends Fragment {

    private PriceListColdDomAdapter mColdDomAdapter;
    private SearchView searchView;

    private List<DomCold> mDomColdList = new ArrayList<>();

    private String month = "";
    private String continent = "";

    private FragmentTruckColdBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTruckColdBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mColdDomAdapter = new PriceListColdDomAdapter(getContext());

        setHasOptionsMenu(true);
        getAllData();
        setAutoComplete();
        return view;
    }

    public void setUpRecyclerView(String m, String c) {
        if (!m.isEmpty() && !c.isEmpty()) {
            mColdDomAdapter.setDomCold(filterData(m, c));
            binding.rcvDomCold.setAdapter(mColdDomAdapter);
            binding.rcvDomCold.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public List<DomCold> filterData(String m, String c) {
        List<DomCold> subList = new ArrayList<>();
        try {
            for (DomCold domCold : mDomColdList) {
                if (domCold.getMonth().equalsIgnoreCase(m) && domCold.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domCold);
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public List<DomCold> filterDataResume(String m, String c, List<DomCold> list) {
        List<DomCold> subList = new ArrayList<>();
        try {
            for (DomCold domCold : list) {
                if (domCold.getMonth().equalsIgnoreCase(m) && domCold.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domCold);
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
        try {
            this.mDomColdList = new ArrayList<>();

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // get path of database name "Users" cotaining users info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cold");
            // get all data from path
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mDomColdList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DomCold domCold = ds.getValue(DomCold.class);
                        // get all users except currently signed is user
                        mDomColdList.add(domCold);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (NullPointerException nullPointerException){
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.rcvDomCold.setAdapter(mColdDomAdapter);
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
        List<DomCold> filteredList = new ArrayList<>();
        for (DomCold domCold : filterData(month, continent)) {
            if (domCold.getAddressReceive().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(domCold);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            mColdDomAdapter.filterList(filteredList);
        }
    }

}