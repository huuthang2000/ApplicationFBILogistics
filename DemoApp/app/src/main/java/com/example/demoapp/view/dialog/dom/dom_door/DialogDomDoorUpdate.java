package com.example.demoapp.view.dialog.dom.dom_door;

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
import com.example.demoapp.databinding.DialogDomDoorUpdateBinding;
import com.example.demoapp.model.DomDoor;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class DialogDomDoorUpdate extends DialogFragment {

    private DialogDomDoorUpdateBinding binding;
    private DomDoor mDomDoor;

    private final String[] listStr = new String[3];

    private String stationGo, stationCome, addressReceive, addressDelivery, productName, weight, quantity, etd;

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
        binding = DialogDomDoorUpdateBinding.inflate(inflater, container, false);
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
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
        }
    }

    public static DialogDomDoorUpdate getInstance() {
        return new DialogDomDoorUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mDomDoor = (DomDoor) bundle.getSerializable(Constants.DOM_DOOR_UPDATE);

            binding.domDoorUpdateAutoContainer.setText(mDomDoor.getType());
            binding.domDoorUpdateAutoMonth.setText(mDomDoor.getMonth());
            binding.domDoorUpdateAutoContinent.setText(mDomDoor.getContinent());

            Objects.requireNonNull(binding.updateDomDoorStationGo.getEditText()).setText(mDomDoor.getStationGo());
            Objects.requireNonNull(binding.updateDomDoorStationCome.getEditText()).setText(mDomDoor.getStationCome());
            Objects.requireNonNull(binding.updateDomDoorAddressReceive.getEditText()).setText(mDomDoor.getAddressReceive());
            Objects.requireNonNull(binding.updateDomDoorAddressDelivery.getEditText()).setText(mDomDoor.getAddressDelivery());
            Objects.requireNonNull(binding.updateDomDoorName.getEditText()).setText(mDomDoor.getProductName());
            Objects.requireNonNull(binding.updateDomDoorWeight.getEditText()).setText(mDomDoor.getWeight());
            Objects.requireNonNull(binding.updateDomDoorQuantity.getEditText()).setText(mDomDoor.getQuantity());
            Objects.requireNonNull(binding.updateDomDoorEtd.getEditText()).setText(mDomDoor.getEtd());

        }
    }

    private void setUpViews() {



        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM_CY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domDoorUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domDoorUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domDoorUpdateAutoContinent.setAdapter(adapterItemsContinent);

        listStr[0] = binding.domDoorUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domDoorUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domDoorUpdateAutoContinent.getText().toString();

        binding.domDoorUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomDoorUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomDoorCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        stationGo = Objects.requireNonNull(binding.updateDomDoorStationGo.getEditText()).getText().toString();
        stationCome = Objects.requireNonNull(binding.updateDomDoorStationCome.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.updateDomDoorAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.updateDomDoorAddressDelivery.getEditText()).getText().toString();
        productName = Objects.requireNonNull(binding.updateDomDoorName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomDoorWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomDoorQuantity.getEditText()).getText().toString();
        etd = Objects.requireNonNull(binding.updateDomDoorEtd.getEditText()).getText().toString();

    }

    public void updateData() {
        getDataFromForm();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("stationGo", stationGo);
        hashMap.put("stationCome", stationCome);
        hashMap.put("addressReceive", addressReceive);
        hashMap.put("addressDelivery", addressDelivery);
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("etd", etd);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        String timeStamp = mDomDoor.getpTime();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Door");
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