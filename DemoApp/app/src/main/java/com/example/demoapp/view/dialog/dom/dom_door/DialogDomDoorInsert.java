package com.example.demoapp.view.dialog.dom.dom_door;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.demoapp.R;
import com.example.demoapp.databinding.DialogDomDoorInsertBinding;
import com.example.demoapp.model.DomDoor;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DialogDomDoorInsert extends DialogFragment implements View.OnClickListener {

    private DialogDomDoorInsertBinding binding;
    private Bundle bundle;

    private final String[] listStr = new String[3];

    private String stationGo, stationCome, addressReceive, addressDelivery, productName, weight, quantity, etd;

    private List<DomDoor> domDoorSeaList;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogDomDoorInsertBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        domDoorSeaList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());

        // get some info of current user to include in post
        userDBRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDBRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setUpViews();
        textWatcher();
        setData();

        return view;
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

    public void setData() {
        bundle = getArguments();
        if (bundle != null) {
            DomDoor mDomDoor = (DomDoor) bundle.getSerializable(Constants.DOM_DOOR_UPDATE);
            if ("YES".equalsIgnoreCase(bundle.getString(Constants.DOM_DOOR_ADD_NEW))) {
                binding.domDoorInsertAutoContainer.setText(mDomDoor.getType());
                binding.domDoorInsertAutoMonth.setText(mDomDoor.getMonth());
                binding.domDoorInsertAutoContinent.setText(mDomDoor.getContinent());

                listStr[0] = binding.domDoorInsertAutoContainer.getText().toString();
                listStr[1] = binding.domDoorInsertAutoMonth.getText().toString();
                listStr[2] = binding.domDoorInsertAutoContinent.getText().toString();

                Objects.requireNonNull(binding.insertDomDoorStationGo.getEditText()).setText(mDomDoor.getStationGo());
                Objects.requireNonNull(binding.insertDomDoorStationCome.getEditText()).setText(mDomDoor.getStationCome());
                Objects.requireNonNull(binding.insertDomDoorAddressReceive.getEditText()).setText(mDomDoor.getAddressReceive());
                Objects.requireNonNull(binding.insertDomDoorAddressDelivery.getEditText()).setText(mDomDoor.getAddressDelivery());
                Objects.requireNonNull(binding.insertDomDoorName.getEditText()).setText(mDomDoor.getProductName());
                Objects.requireNonNull(binding.insertDomDoorWeight.getEditText()).setText(mDomDoor.getWeight());
                Objects.requireNonNull(binding.insertDomDoorQuantity.getEditText()).setText(mDomDoor.getQuantity());
                Objects.requireNonNull(binding.insertDomDoorEtd.getEditText()).setText(mDomDoor.getEtd());

            }
        }
    }

    public static DialogDomDoorInsert getInstance() {

        return new DialogDomDoorInsert();
    }

    private void setUpViews() {

        binding.btnDomDoorInsert.setOnClickListener(this);
        binding.btnDomDoorCancel.setOnClickListener(this);

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM_CY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domDoorInsertAutoContainer.setAdapter(adapterItemsType);
        binding.domDoorInsertAutoMonth.setAdapter(adapterItemsMonth);
        binding.domDoorInsertAutoContinent.setAdapter(adapterItemsContinent);

        binding.domDoorInsertAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorInsertAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domDoorInsertAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    /**
     * Get current date and time
     *
     * @return current date and time
     */
    public String getCreatedDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_dom_door_insert:
                if (isFilled()) {
                    insertData();
                    dismiss();
                } else
                    Toast.makeText(getContext(), Constants.INSERT_FAILED, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_dom_door_cancel:
                dismiss();
                break;
        }
    }

    public void getDataFromForm() {
        stationGo = Objects.requireNonNull(binding.insertDomDoorStationGo.getEditText()).getText().toString();
        stationCome = Objects.requireNonNull(binding.insertDomDoorStationCome.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.insertDomDoorAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.insertDomDoorAddressDelivery.getEditText()).getText().toString();
        productName = Objects.requireNonNull(binding.insertDomDoorName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.insertDomDoorWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.insertDomDoorQuantity.getEditText()).getText().toString();
        etd = Objects.requireNonNull(binding.insertDomDoorEtd.getEditText()).getText().toString();
    }

    public void insertData() {
        getDataFromForm();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("productName", productName);
        hashMap.put("stationGo", stationGo);
        hashMap.put("stationCome", stationCome);
        hashMap.put("addressReceive", addressReceive);
        hashMap.put("addressDelivery", addressDelivery);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("etd", etd);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        hashMap.put("createdDate", getCreatedDate());
        hashMap.put("pTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Door");
        // put data in this ref
        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean isFilled() {
        boolean result = true;

        if (TextUtils.isEmpty(binding.domDoorInsertAutoContainer.getText())) {
            result = false;
            binding.domDoorInsertAutoContainer.setError(Constants.ERROR_AUTO_COMPLETE_TYPE);
        }

        if (TextUtils.isEmpty(binding.domDoorInsertAutoMonth.getText())) {
            result = false;
            binding.domDoorInsertAutoMonth.setError(Constants.ERROR_AUTO_COMPLETE_MONTH);
        }

        if (TextUtils.isEmpty(binding.domDoorInsertAutoContinent.getText())) {
            result = false;
            binding.domDoorInsertAutoContinent.setError(Constants.ERROR_AUTO_COMPLETE_CONTINENT);
        }

        return result;
    }

    /**
     * If this field is not empty, set null for error
     */
    public void textWatcher() {

        binding.domDoorInsertAutoContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDoorInsertAutoContainer.getText())) {
                    binding.domDoorInsertAutoContainer.setError(null);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domDoorInsertAutoMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDoorInsertAutoMonth.getText())) {
                    binding.domDoorInsertAutoMonth.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domDoorInsertAutoContinent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDoorInsertAutoContinent.getText())) {
                    binding.domDoorInsertAutoContinent.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
