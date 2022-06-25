package com.example.demoapp.view.fragment.sales.domwaterway;

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
import com.example.demoapp.adapter.sale.PriceListCySeaDomAdapter;
import com.example.demoapp.databinding.FragmentCyWaterWayBinding;
import com.example.demoapp.model.DomCySea;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.viewmodel.CommunicateViewModel;
import com.example.demoapp.viewmodel.DomCySeaViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CyWaterWayFragment extends Fragment {
    private FragmentCyWaterWayBinding binding;
    private DomCySeaViewModel mDomCySeaViewModel;
    private PriceListCySeaDomAdapter mCySeaDomAdapter;
    private SearchView searchView;

    private List<DomCySea> mDomCySeaList = new ArrayList<>();

    private String month = "";
    private String continent = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCyWaterWayBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mCySeaDomAdapter = new PriceListCySeaDomAdapter(getContext());
        mDomCySeaViewModel = new ViewModelProvider(this).get(DomCySeaViewModel.class);

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
            mCySeaDomAdapter.setDomCySea(filterData(m, c));
            binding.rcvDomCySea.setAdapter(mCySeaDomAdapter);
            binding.rcvDomCySea.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public List<DomCySea> filterData(String m, String c) {
        List<DomCySea> subList = new ArrayList<>();
        try {
            for (DomCySea domCySea : mDomCySeaList) {
                if (domCySea.getMonth().equalsIgnoreCase(m) && domCySea.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domCySea);
                }
            }
        } catch (NullPointerException nullPointerException) {
            Toast.makeText(getContext(), nullPointerException.toString(), Toast.LENGTH_LONG).show();
        }
        return subList;
    }

    public List<DomCySea> filterDataResume(String m, String c, List<DomCySea> list) {
        List<DomCySea> subList = new ArrayList<>();
        try {
            for (DomCySea domCySea : list) {
                if (domCySea.getMonth().equalsIgnoreCase(m) && domCySea.getContinent().equalsIgnoreCase(c)) {
                    subList.add(domCySea);
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
            this.mDomCySeaList = new ArrayList<>();

            // get current user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // get path of database name "Users" cotaining users info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cy_Sea");
            // get all data from path
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mDomCySeaList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        DomCySea domCySea = ds.getValue(DomCySea.class);
                        // get all users except currently signed is user
                        mDomCySeaList.add(domCySea);
                    }
                    sortDomCySea(mDomCySeaList);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (NullPointerException nullPointerException){
            Toast.makeText(getContext(), nullPointerException.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public List<DomCySea> sortDomCySea(List<DomCySea> list){
        List<DomCySea> result = new ArrayList<>();
        for(int i = list.size()-1; i>=0; i--){
            result.add(list.get(i));
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        mDomCySeaViewModel.getAllData().observe(getViewLifecycleOwner(), domCySeas -> mCySeaDomAdapter.setDomCySea(filterDataResume(month, continent, domCySeas)));

        binding.rcvDomCySea.setAdapter(mCySeaDomAdapter);
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
    private void filter(String text){
        List<DomCySea> filteredList = new ArrayList<>();
        for( DomCySea domCySea: filterData(month, continent)){
            if(domCySea.getPortGo().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(domCySea);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
        }else {
            mCySeaDomAdapter.filterList(filteredList);
        }
    }


}