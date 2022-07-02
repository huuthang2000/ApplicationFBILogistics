package com.example.demoapp.view.dialog.dom.dom_export;

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
import com.example.demoapp.databinding.FragmentDialogDomExportUpdateBinding;
import com.example.demoapp.model.DomExport;
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

public class DialogDomExportUpdate extends DialogFragment {

    private FragmentDialogDomExportUpdateBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference userDBRef;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    private final String[] listStr = new String[3];
    private DomExport mDomExport;

    private String productName, weight, quantity, temp, address, portExport, length, height, width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDialogDomExportUpdateBinding.inflate(inflater, container, false);
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

    public static DialogDomExportUpdate getInstance() {
        return new DialogDomExportUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mDomExport = (DomExport) bundle.getSerializable(Constants.DOM_EXPORT_UPDATE);

            binding.domExportUpdateAutoContainer.setText(mDomExport.getType());
            binding.domExportUpdateAutoMonth.setText(mDomExport.getMonth());
            binding.domExportUpdateAutoContinent.setText(mDomExport.getContinent());

            Objects.requireNonNull(binding.updateDomExportName.getEditText()).setText(mDomExport.getProductName());
            Objects.requireNonNull(binding.updateDomExportWeight.getEditText()).setText(mDomExport.getWeight());
            Objects.requireNonNull(binding.updateDomExportQuantity.getEditText()).setText(mDomExport.getQuantity());
            Objects.requireNonNull(binding.updateDomExportTemp.getEditText()).setText(mDomExport.getTemp());
            Objects.requireNonNull(binding.updateDomExportAddress.getEditText()).setText(mDomExport.getAddress());
            Objects.requireNonNull(binding.updateDomExportPort.getEditText()).setText(mDomExport.getPortExport());
            Objects.requireNonNull(binding.updateDomExportLength.getEditText()).setText(mDomExport.getLength());
            Objects.requireNonNull(binding.updateDomExportHeight.getEditText()).setText(mDomExport.getHeight());
            Objects.requireNonNull(binding.updateDomExportWidth.getEditText()).setText(mDomExport.getWidth());
        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        listStr[0] = binding.domExportUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domExportUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domExportUpdateAutoContinent.getText().toString();

        binding.domExportUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domExportUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domExportUpdateAutoContinent.setAdapter(adapterItemsContinent);

        binding.domExportUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domExportUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domExportUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomExportUpdateUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomExportUpdateCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.updateDomExportName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomExportWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomExportQuantity.getEditText()).getText().toString();
        temp = Objects.requireNonNull(binding.updateDomExportTemp.getEditText()).getText().toString();
        address = Objects.requireNonNull(binding.updateDomExportAddress.getEditText()).getText().toString();
        portExport = Objects.requireNonNull(binding.updateDomExportPort.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.updateDomExportLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.updateDomExportHeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.updateDomExportWidth.getEditText()).getText().toString();
    }

    public void updateData() {
        getDataFromForm();

        String timeStamp = mDomExport.getpTime();
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Export");
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