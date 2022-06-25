package com.example.demoapp.view.dialog.dom.dom_cold;

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
import com.example.demoapp.databinding.DialogDomColdInsertBinding;
import com.example.demoapp.model.DomCold;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.activity.LoginActivity;
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

public class DialogDomColdInsert extends DialogFragment implements View.OnClickListener {

    private DialogDomColdInsertBinding binding;
    private Bundle bundle;

    private final String[] listStr = new String[3];

    private String productName, weight, quantityPallet, quantityCarton, addressReceive, addressDelivery, length, height, width;

    private List<DomCold> domColds;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogDomColdInsertBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        domColds = new ArrayList<>();
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
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }

    public void setData() {
        bundle = getArguments();
        if (bundle != null) {
            DomCold mDomCold = (DomCold) bundle.getSerializable(Constants.DOM_COLD_UPDATE);
            if ("YES".equalsIgnoreCase(bundle.getString(Constants.DOM_COLD_ADD_NEW))) {
                binding.domColdInsertAutoContainer.setText(mDomCold.getType());
                binding.domColdInsertAutoMonth.setText(mDomCold.getMonth());
                binding.domColdInsertAutoContinent.setText(mDomCold.getContinent());

                listStr[0] = binding.domColdInsertAutoContainer.getText().toString();
                listStr[1] = binding.domColdInsertAutoMonth.getText().toString();
                listStr[2] = binding.domColdInsertAutoContinent.getText().toString();

                Objects.requireNonNull(binding.insertDomColdName.getEditText()).setText(mDomCold.getProductName());
                Objects.requireNonNull(binding.insertDomColdWeight.getEditText()).setText(mDomCold.getWeight());
                Objects.requireNonNull(binding.insertDomColdQuantityPallet.getEditText()).setText(mDomCold.getQuantityPallet());
                Objects.requireNonNull(binding.insertDomColdQuantityCarton.getEditText()).setText(mDomCold.getQuantityCarton());
                Objects.requireNonNull(binding.insertDomColdAddressReceive.getEditText()).setText(mDomCold.getAddressReceive());
                Objects.requireNonNull(binding.insertDomColdAddressDelivery.getEditText()).setText(mDomCold.getAddressDelivery());
                Objects.requireNonNull(binding.insertDomColdLength.getEditText()).setText(mDomCold.getLength());
                Objects.requireNonNull(binding.insertDomColdHeight.getEditText()).setText(mDomCold.getHeight());
                Objects.requireNonNull(binding.insertDomColdWidth.getEditText()).setText(mDomCold.getWidth());
            }
        }
    }

    public static DialogDomColdInsert getInstance() {

        return new DialogDomColdInsert();
    }

    private void setUpViews() {

        binding.btnDomColdInsert.setOnClickListener(this);
        binding.btnDomColdCancel.setOnClickListener(this);

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_TYPE_DOM_DRY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domColdInsertAutoContainer.setAdapter(adapterItemsType);
        binding.domColdInsertAutoMonth.setAdapter(adapterItemsMonth);
        binding.domColdInsertAutoContinent.setAdapter(adapterItemsContinent);

        binding.domColdInsertAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domColdInsertAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domColdInsertAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
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
            case R.id.btn_dom_cold_insert:
                if (isFilled()) {
                    insertData();
                    dismiss();
                } else
                    Toast.makeText(getContext(), Constants.INSERT_FAILED, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_dom_cold_cancel:
                dismiss();
                break;
        }
    }

    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.insertDomColdName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.insertDomColdWeight.getEditText()).getText().toString();
        quantityPallet = Objects.requireNonNull(binding.insertDomColdQuantityPallet.getEditText()).getText().toString();
        quantityCarton = Objects.requireNonNull(binding.insertDomColdQuantityCarton.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.insertDomColdAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.insertDomColdAddressDelivery.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.insertDomColdLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.insertDomColdHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.insertDomColdWidth.getEditText()).getText().toString();
    }

    public void insertData() {
        getDataFromForm();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("quantityPallet", quantityPallet);
        hashMap.put("quantityCarton", quantityCarton);
        hashMap.put("addressReceive", addressReceive);
        hashMap.put("addressDelivery", addressDelivery);
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("length", length);
        hashMap.put("height", height);
        hashMap.put("width", width);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        hashMap.put("createdDate", getCreatedDate());
        hashMap.put("pTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cold");
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

        if (TextUtils.isEmpty(binding.domColdInsertAutoContainer.getText())) {
            result = false;
            binding.domColdInsertAutoContainer.setError(Constants.ERROR_AUTO_COMPLETE_TYPE);
        }

        if (TextUtils.isEmpty(binding.domColdInsertAutoMonth.getText())) {
            result = false;
            binding.domColdInsertAutoMonth.setError(Constants.ERROR_AUTO_COMPLETE_MONTH);
        }

        if (TextUtils.isEmpty(binding.domColdInsertAutoContinent.getText())) {
            result = false;
            binding.domColdInsertAutoContinent.setError(Constants.ERROR_AUTO_COMPLETE_CONTINENT);
        }

        return result;
    }

    /**
     * If this field is not empty, set null for error
     */
    public void textWatcher() {

        binding.domColdInsertAutoContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domColdInsertAutoContainer.getText())) {
                    binding.domColdInsertAutoContainer.setError(null);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domColdInsertAutoMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domColdInsertAutoMonth.getText())) {
                    binding.domColdInsertAutoMonth.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domColdInsertAutoContinent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domColdInsertAutoContinent.getText())) {
                    binding.domColdInsertAutoContinent.setError(null);
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
