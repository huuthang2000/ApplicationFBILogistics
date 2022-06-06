package com.example.demoapp.view.dialog.dom.dom_door_sea;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.demoapp.R;
import com.example.demoapp.databinding.DialogDomDoorSeaUpdateBinding;
import com.example.demoapp.model.DomDoorSea;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.activity.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class DialogDomDoorSeaUpdate extends DialogFragment {

    private DialogDomDoorSeaUpdateBinding binding;
    private DomDoorSea mDomDoorSea;

    private final String[] listStr = new String[3];

    private String portGo, portCome, addressReceive, addressDelivery, productName, weight, quantity, etd;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DialogDomDoorSeaUpdateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        progressDialog = new ProgressDialog(getContext());

        setData();
        setUpViews();
        setListenerForButtons();

        return root;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            uid = user.getUid();
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }

    public static DialogDomDoorSeaUpdate getInstance() {
        return new DialogDomDoorSeaUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mDomDoorSea = (DomDoorSea) bundle.getSerializable(Constants.DOM_DOOR_SEA_UPDATE);

            binding.domDoorSeaUpdateAutoContainer.setText(mDomDoorSea.getType());
            binding.domDoorSeaUpdateAutoMonth.setText(mDomDoorSea.getMonth());
            binding.domDoorSeaUpdateAutoContinent.setText(mDomDoorSea.getContinent());

            Objects.requireNonNull(binding.updateDomDoorSeaPortGo.getEditText()).setText(mDomDoorSea.getPortGo());
            Objects.requireNonNull(binding.updateDomDoorSeaPortCome.getEditText()).setText(mDomDoorSea.getPortCome());
            Objects.requireNonNull(binding.updateDomDoorSeaAddressReceive.getEditText()).setText(mDomDoorSea.getAddressReceive());
            Objects.requireNonNull(binding.updateDomDoorSeaAddressDelivery.getEditText()).setText(mDomDoorSea.getAddressDelivery());
            Objects.requireNonNull(binding.updateDomDoorSeaName.getEditText()).setText(mDomDoorSea.getProductName());
            Objects.requireNonNull(binding.updateDomDoorSeaWeight.getEditText()).setText(mDomDoorSea.getWeight());
            Objects.requireNonNull(binding.updateDomDoorSeaQuantity.getEditText()).setText(mDomDoorSea.getQuantity());
            Objects.requireNonNull(binding.updateDomDoorSeaEtd.getEditText()).setText(mDomDoorSea.getEtd());

        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM_SEA);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domDoorSeaUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domDoorSeaUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domDoorSeaUpdateAutoContinent.setAdapter(adapterItemsContinent);

        listStr[0] = binding.domDoorSeaUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domDoorSeaUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domDoorSeaUpdateAutoContinent.getText().toString();

        binding.domDoorSeaUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorSeaUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorSeaUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomDoorSeaUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomDoorSeaCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        portGo = Objects.requireNonNull(binding.updateDomDoorSeaPortGo.getEditText()).getText().toString();
        portCome = Objects.requireNonNull(binding.updateDomDoorSeaPortCome.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.updateDomDoorSeaAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.updateDomDoorSeaAddressDelivery.getEditText()).getText().toString();
        productName = Objects.requireNonNull(binding.updateDomDoorSeaName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomDoorSeaWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomDoorSeaQuantity.getEditText()).getText().toString();
        etd = Objects.requireNonNull(binding.updateDomDoorSeaEtd.getEditText()).getText().toString();

    }

    public void updateData() {
        getDataFromForm();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("portGo", portGo);
        hashMap.put("portCome", portCome);
        hashMap.put("addressReceive", addressReceive);
        hashMap.put("addressDelivery", addressDelivery);
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("etd", etd);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        String timeStamp = mDomDoorSea.getpTime();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Door_Sea");
        ref.child(timeStamp)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}