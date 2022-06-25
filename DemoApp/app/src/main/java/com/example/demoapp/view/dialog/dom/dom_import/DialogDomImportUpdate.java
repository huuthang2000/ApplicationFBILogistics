package com.example.demoapp.view.dialog.dom.dom_import;

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
import com.example.demoapp.databinding.DialogDomImportUpdateBinding;
import com.example.demoapp.model.DomImport;
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

public class DialogDomImportUpdate extends DialogFragment {

    private DialogDomImportUpdateBinding binding;


    private final String[] listStr = new String[3];
    private DomImport mDomImport;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    private String productName, weight, quantity, temp, address, portReceive, length, height, width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DialogDomImportUpdateBinding.inflate(inflater, container, false);
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

    public static DialogDomImportUpdate getInstance() {
        return new DialogDomImportUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mDomImport = (DomImport) bundle.getSerializable(Constants.DOM_IMPORT_UPDATE);

            binding.domImportUpdateAutoContainer.setText(mDomImport.getType());
            binding.domImportUpdateAutoMonth.setText(mDomImport.getMonth());
            binding.domImportUpdateAutoContinent.setText(mDomImport.getContinent());

            Objects.requireNonNull(binding.updateDomImportName.getEditText()).setText(mDomImport.getProductName());
            Objects.requireNonNull(binding.updateDomImportWeight.getEditText()).setText(mDomImport.getWeight());
            Objects.requireNonNull(binding.updateDomImportQuantity.getEditText()).setText(mDomImport.getQuantity());
            Objects.requireNonNull(binding.updateDomImportTemp.getEditText()).setText(mDomImport.getTemp());
            Objects.requireNonNull(binding.updateDomImportAddress.getEditText()).setText(mDomImport.getAddress());
            Objects.requireNonNull(binding.updateDomImportPort.getEditText()).setText(mDomImport.getPortReceive());
            Objects.requireNonNull(binding.updateDomImportLength.getEditText()).setText(mDomImport.getLength());
            Objects.requireNonNull(binding.updateDomImportHeight.getEditText()).setText(mDomImport.getHeight());
            Objects.requireNonNull(binding.updateDomImportWidth.getEditText()).setText(mDomImport.getWidth());
        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        listStr[0] = binding.domImportUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domImportUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domImportUpdateAutoContinent.getText().toString();

        binding.domImportUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domImportUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domImportUpdateAutoContinent.setAdapter(adapterItemsContinent);

        binding.domImportUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domImportUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domImportUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomImportUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomImportCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.updateDomImportName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomImportWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomImportQuantity.getEditText()).getText().toString();
        temp = Objects.requireNonNull(binding.updateDomImportTemp.getEditText()).getText().toString();
        address = Objects.requireNonNull(binding.updateDomImportAddress.getEditText()).getText().toString();
        portReceive = Objects.requireNonNull(binding.updateDomImportPort.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.updateDomImportLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.updateDomImportHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.updateDomImportWidth.getEditText()).getText().toString();
    }

    public void updateData() {
        getDataFromForm();

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

        String timeStamp = mDomImport.getpTime();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Import");
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