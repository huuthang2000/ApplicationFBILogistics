package com.example.demoapp.view.dialog.dom.dom_export;

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
import com.example.demoapp.databinding.DialogInsertDomExportBinding;
import com.example.demoapp.model.DomExport;
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

public class DialogDomExportInsert extends DialogFragment implements View.OnClickListener {

    private DialogInsertDomExportBinding binding;
    private Bundle bundle;

    private final String[] listStr = new String[3];

    private String productName, weight, quantity, temp, address, portExport, length, height, width;

    private List<DomExport> domExportList;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogInsertDomExportBinding.inflate(inflater, container, false);

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
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }

    public void setData() {
        bundle = getArguments();
        if (bundle != null) {
            DomExport mDomExport = (DomExport) bundle.getSerializable(Constants.DOM_EXPORT_UPDATE);
            if ("YES".equalsIgnoreCase(bundle.getString(Constants.DOM_EXPORT_ADD_NEW))) {
                binding.domExportInsertAutoContainer.setText(mDomExport.getType());
                binding.domExportInsertAutoMonth.setText(mDomExport.getMonth());
                binding.domExportInsertAutoContinent.setText(mDomExport.getContinent());

                listStr[0] = binding.domExportInsertAutoContainer.getText().toString();
                listStr[1] = binding.domExportInsertAutoMonth.getText().toString();
                listStr[2] = binding.domExportInsertAutoContinent.getText().toString();

                Objects.requireNonNull(binding.insertDomExportName.getEditText()).setText(mDomExport.getProductName());
                Objects.requireNonNull(binding.insertDomExportWeight.getEditText()).setText(mDomExport.getWeight());
                Objects.requireNonNull(binding.insertDomExportQuantity.getEditText()).setText(mDomExport.getQuantity());
                Objects.requireNonNull(binding.insertDomExportTemp.getEditText()).setText(mDomExport.getTemp());
                Objects.requireNonNull(binding.insertDomExportAddress.getEditText()).setText(mDomExport.getAddress());
                Objects.requireNonNull(binding.insertDomExportPort.getEditText()).setText(mDomExport.getPortExport());
                Objects.requireNonNull(binding.insertDomExportLength.getEditText()).setText(mDomExport.getLength());
                Objects.requireNonNull(binding.insertDomExportHeight.getEditText()).setText(mDomExport.getHeight());
                Objects.requireNonNull(binding.insertDomExportWidth.getEditText()).setText(mDomExport.getWidth());
            }
        }
    }

    public static DialogDomExportInsert getInstance() {

        return new DialogDomExportInsert();
    }

    private void setUpViews() {

        binding.btnDomExportInsert.setOnClickListener(this);
        binding.btnDomExportCancel.setOnClickListener(this);

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domExportInsertAutoContainer.setAdapter(adapterItemsType);
        binding.domExportInsertAutoMonth.setAdapter(adapterItemsMonth);
        binding.domExportInsertAutoContinent.setAdapter(adapterItemsContinent);

        binding.domExportInsertAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domExportInsertAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domExportInsertAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
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
            case R.id.btn_dom_export_insert:
                if (isFilled()) {
                    insertData();
                    dismiss();
                } else
                    Toast.makeText(getContext(), Constants.INSERT_FAILED, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_dom_export_cancel:
                dismiss();
                break;
        }
    }

    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.insertDomExportName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.insertDomExportWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.insertDomExportQuantity.getEditText()).getText().toString();
        temp = Objects.requireNonNull(binding.insertDomExportTemp.getEditText()).getText().toString();
        address = Objects.requireNonNull(binding.insertDomExportAddress.getEditText()).getText().toString();
        portExport = Objects.requireNonNull(binding.insertDomExportPort.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.insertDomExportLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.insertDomExportHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.insertDomExportWidth.getEditText()).getText().toString();
    }

    public void insertData() {
        getDataFromForm();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("temp", temp);
        hashMap.put("address", address);
        hashMap.put("portExport", portExport);
        hashMap.put("length", length);
        hashMap.put("height", height);
        hashMap.put("width", width);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        hashMap.put("createdDate", getCreatedDate());
        hashMap.put("pTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Export");
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

        if (TextUtils.isEmpty(binding.domExportInsertAutoContainer.getText())) {
            result = false;
            binding.domExportInsertAutoContainer.setError(Constants.ERROR_AUTO_COMPLETE_TYPE);
        }

        if (TextUtils.isEmpty(binding.domExportInsertAutoMonth.getText())) {
            result = false;
            binding.domExportInsertAutoMonth.setError(Constants.ERROR_AUTO_COMPLETE_MONTH);
        }

        if (TextUtils.isEmpty(binding.domExportInsertAutoContinent.getText())) {
            result = false;
            binding.domExportInsertAutoContinent.setError(Constants.ERROR_AUTO_COMPLETE_CONTINENT);
        }

        return result;
    }

    /**
     * If this field is not empty, set null for error
     */
    public void textWatcher() {

        binding.domExportInsertAutoContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domExportInsertAutoContainer.getText())) {
                    binding.domExportInsertAutoContainer.setError(null);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domExportInsertAutoMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domExportInsertAutoMonth.getText())) {
                    binding.domExportInsertAutoMonth.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domExportInsertAutoContinent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domExportInsertAutoContinent.getText())) {
                    binding.domExportInsertAutoContinent.setError(null);
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
