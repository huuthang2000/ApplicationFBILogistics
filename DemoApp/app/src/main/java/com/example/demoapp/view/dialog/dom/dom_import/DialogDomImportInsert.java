package com.example.demoapp.view.dialog.dom.dom_import;

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
import com.example.demoapp.databinding.DialogDomImportInsertBinding;
import com.example.demoapp.model.DomExport;
import com.example.demoapp.model.DomImport;
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

public class DialogDomImportInsert extends DialogFragment{

    private DialogDomImportInsertBinding binding;
    private Bundle bundle;

    private final String[] listStr = new String[3];

    private String productName, weight, quantity, temp, address, portReceive, length, height, width;

    private List<DomExport> domExportList;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogDomImportInsertBinding.inflate(inflater, container, false);

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
            DomImport mDomImport = (DomImport) bundle.getSerializable(Constants.DOM_IMPORT_UPDATE);
            if ("YES".equalsIgnoreCase(bundle.getString(Constants.DOM_IMPORT_ADD_NEW))) {

                binding.domImportInsertAutoContainer.setText(mDomImport.getType());
                binding.domImportInsertAutoMonth.setText(mDomImport.getMonth());
                binding.domImportInsertAutoContinent.setText(mDomImport.getContinent());

                listStr[0] = binding.domImportInsertAutoContainer.getText().toString();
                listStr[1] = binding.domImportInsertAutoMonth.getText().toString();
                listStr[2] = binding.domImportInsertAutoContinent.getText().toString();

                Objects.requireNonNull(binding.insertDomImportName.getEditText()).setText(mDomImport.getProductName());
                Objects.requireNonNull(binding.insertDomImportWeight.getEditText()).setText(mDomImport.getWeight());
                Objects.requireNonNull(binding.insertDomImportQuantity.getEditText()).setText(mDomImport.getQuantity());
                Objects.requireNonNull(binding.insertDomImportTemp.getEditText()).setText(mDomImport.getTemp());
                Objects.requireNonNull(binding.insertDomImportAddress.getEditText()).setText(mDomImport.getAddress());
                Objects.requireNonNull(binding.insertDomImportPort.getEditText()).setText(mDomImport.getPortReceive());
                Objects.requireNonNull(binding.insertDomImportLength.getEditText()).setText(mDomImport.getLength());
                Objects.requireNonNull(binding.insertDomImportHeight.getEditText()).setText(mDomImport.getHeight());
                Objects.requireNonNull(binding.insertDomImportWidth.getEditText()).setText(mDomImport.getWidth());
            }
        }
    }

    public static DialogDomImportInsert getInstance() {

        return new DialogDomImportInsert();
    }

    private void setUpViews() {

        binding.btnDomImportInsert.setOnClickListener(view -> {
            if (isFilled()) {
                insertData();
                dismiss();
            } else
                Toast.makeText(getContext(), Constants.INSERT_FAILED, Toast.LENGTH_LONG).show();
        });

        binding.btnDomImportCancel.setOnClickListener(view -> dismiss());


        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domImportInsertAutoContainer.setAdapter(adapterItemsType);
        binding.domImportInsertAutoMonth.setAdapter(adapterItemsMonth);
        binding.domImportInsertAutoContinent.setAdapter(adapterItemsContinent);

        binding.domImportInsertAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domImportInsertAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domImportInsertAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
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

    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.insertDomImportName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.insertDomImportWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.insertDomImportQuantity.getEditText()).getText().toString();
        temp = Objects.requireNonNull(binding.insertDomImportTemp.getEditText()).getText().toString();
        address = Objects.requireNonNull(binding.insertDomImportAddress.getEditText()).getText().toString();
        portReceive = Objects.requireNonNull(binding.insertDomImportPort.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.insertDomImportLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.insertDomImportHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.insertDomImportWidth.getEditText()).getText().toString();
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
        hashMap.put("portReceive", portReceive);
        hashMap.put("length", length);
        hashMap.put("height", height);
        hashMap.put("width", width);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);
        hashMap.put("createdDate", getCreatedDate());
        hashMap.put("pTime", timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Import");
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

        if (TextUtils.isEmpty(binding.domImportInsertAutoContainer.getText())) {
            result = false;
            binding.domImportInsertAutoContainer.setError(Constants.ERROR_AUTO_COMPLETE_TYPE);
        }

        if (TextUtils.isEmpty(binding.domImportInsertAutoMonth.getText())) {
            result = false;
            binding.domImportInsertAutoMonth.setError(Constants.ERROR_AUTO_COMPLETE_MONTH);
        }

        if (TextUtils.isEmpty(binding.domImportInsertAutoContinent.getText())) {
            result = false;
            binding.domImportInsertAutoContinent.setError(Constants.ERROR_AUTO_COMPLETE_CONTINENT);
        }

        return result;
    }

    /**
     * If this field is not empty, set null for error
     */
    public void textWatcher() {

        binding.domImportInsertAutoContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domImportInsertAutoContainer.getText())) {
                    binding.domImportInsertAutoContainer.setError(null);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domImportInsertAutoMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domImportInsertAutoMonth.getText())) {
                    binding.domImportInsertAutoMonth.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.domImportInsertAutoContinent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.domImportInsertAutoContinent.getText())) {
                    binding.domImportInsertAutoContinent.setError(null);
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
