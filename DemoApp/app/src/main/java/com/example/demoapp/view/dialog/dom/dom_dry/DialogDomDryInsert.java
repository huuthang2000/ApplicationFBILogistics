package com.example.demoapp.view.dialog.dom.dom_dry;

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
import com.example.demoapp.databinding.DialogDomDryInsertBinding;
import com.example.demoapp.model.DomDry;
import com.example.demoapp.model.DomExport;
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

public class DialogDomDryInsert extends DialogFragment implements View.OnClickListener {

    private DialogDomDryInsertBinding binding;
    private Bundle bundle;

    private final String[] listStr = new String[3];

    private String productName, weight, quantityPallet, quantityCarton, addressReceive, addressDelivery, length, height, width;

    private List<DomExport> domExportList;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogDomDryInsertBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        domExportList = new ArrayList<>();
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
            DomDry mDomDry = (DomDry) bundle.getSerializable(Constants.DOM_DRY_UPDATE);
            if ("YES".equalsIgnoreCase(bundle.getString(Constants.DOM_DRY_ADD_NEW))) {
                binding.domDryInsertAutoContainer.setText(mDomDry.getType());
                binding.domDryInsertAutoMonth.setText(mDomDry.getMonth());
                binding.domDryInsertAutoContinent.setText(mDomDry.getContinent());

                listStr[0] = binding.domDryInsertAutoContainer.getText().toString();
                listStr[1] = binding.domDryInsertAutoMonth.getText().toString();
                listStr[2] = binding.domDryInsertAutoContinent.getText().toString();

                Objects.requireNonNull(binding.insertDomDryName.getEditText()).setText(mDomDry.getProductName());
                Objects.requireNonNull(binding.insertDomDryWeight.getEditText()).setText(mDomDry.getWeight());
                Objects.requireNonNull(binding.insertDomDryQuantityPallet.getEditText()).setText(mDomDry.getQuantityPallet());
                Objects.requireNonNull(binding.insertDomDryQuantityCarton.getEditText()).setText(mDomDry.getQuantityCarton());
                Objects.requireNonNull(binding.insertDomDryAddressReceive.getEditText()).setText(mDomDry.getAddressReceive());
                Objects.requireNonNull(binding.insertDomDryAddressDelivery.getEditText()).setText(mDomDry.getAddressDelivery());
                Objects.requireNonNull(binding.insertDomDryLength.getEditText()).setText(mDomDry.getLength());
                Objects.requireNonNull(binding.insertDomDryHeight.getEditText()).setText(mDomDry.getHeight());
                Objects.requireNonNull(binding.insertDomDryWidth.getEditText()).setText(mDomDry.getWidth());
            }
        }
    }

    public static DialogDomDryInsert getInstance() {

        return new DialogDomDryInsert();
    }

    private void setUpViews() {

        binding.btnDomDryInsert.setOnClickListener(this);
        binding.btnDomDryCancel.setOnClickListener(this);

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_TYPE_DOM_DRY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domDryInsertAutoContainer.setAdapter(adapterItemsType);
        binding.domDryInsertAutoMonth.setAdapter(adapterItemsMonth);
        binding.domDryInsertAutoContinent.setAdapter(adapterItemsContinent);

        binding.domDryInsertAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domDryInsertAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domDryInsertAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    /**
     * Get current date and time
     *
     * @return current date and time
     */
    private String getCreatedDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_dom_dry_insert:
                if (isFilled()) {
                    insertData();
                    dismiss();
                } else
                    Toast.makeText(getContext(), Constants.INSERT_FAILED, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_dom_dry_cancel:
                dismiss();
                break;
        }
    }

    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.insertDomDryName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.insertDomDryWeight.getEditText()).getText().toString();
        quantityPallet = Objects.requireNonNull(binding.insertDomDryQuantityPallet.getEditText()).getText().toString();
        quantityCarton = Objects.requireNonNull(binding.insertDomDryQuantityCarton.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.insertDomDryAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.insertDomDryAddressDelivery.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.insertDomDryLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.insertDomDryHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.insertDomDryWidth.getEditText()).getText().toString();
    }

    public void insertData() {
        getDataFromForm();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantityPallet", quantityPallet);
        hashMap.put("quantityCarton", quantityCarton);
        hashMap.put("addressReceive", addressReceive);
        hashMap.put("addressDelivery", addressDelivery);
        hashMap.put("length", length);
        hashMap.put("height", height);
        hashMap.put("width", width);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        hashMap.put("createdDate", getCreatedDate());
        hashMap.put("pTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Dry");
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

        if (TextUtils.isEmpty(binding.domDryInsertAutoContainer.getText())) {
            result = false;
            binding.domDryInsertAutoContainer.setError(Constants.ERROR_AUTO_COMPLETE_TYPE);
        }

        if (TextUtils.isEmpty(binding.domDryInsertAutoMonth.getText())) {
            result = false;
            binding.domDryInsertAutoMonth.setError(Constants.ERROR_AUTO_COMPLETE_MONTH);
        }

        if (TextUtils.isEmpty(binding.domDryInsertAutoContinent.getText())) {
            result = false;
            binding.domDryInsertAutoContinent.setError(Constants.ERROR_AUTO_COMPLETE_CONTINENT);
        }

        return result;
    }

    /**
     * If this field is not empty, set null for error
     */
    public void textWatcher() {

        binding.domDryInsertAutoContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDryInsertAutoContainer.getText())) {
                    binding.domDryInsertAutoContainer.setError(null);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domDryInsertAutoMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDryInsertAutoMonth.getText())) {
                    binding.domDryInsertAutoMonth.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domDryInsertAutoContinent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domDryInsertAutoContinent.getText())) {
                    binding.domDryInsertAutoContinent.setError(null);
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
