package com.example.demoapp.view.dialog.dom.dom_cold;

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
import com.example.demoapp.databinding.DialogDomColdUpdateBinding;
import com.example.demoapp.model.DomCold;
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

public class DialogDomColdUpdate extends DialogFragment {

    private DialogDomColdUpdateBinding binding;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    // user info
    String name, email, uid, dp;

    private final String[] listStr = new String[3];
    private DomCold mDomCold;

    private String productName, weight, quantityPallet, quantityCarton, addressReceive, addressDelivery, length, height, width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DialogDomColdUpdateBinding.inflate(inflater, container, false);
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

    public static DialogDomColdUpdate getInstance() {
        return new DialogDomColdUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mDomCold = (DomCold) bundle.getSerializable(Constants.DOM_COLD_UPDATE);

            binding.domColdUpdateAutoContainer.setText(mDomCold.getType());
            binding.domColdUpdateAutoMonth.setText(mDomCold.getMonth());
            binding.domColdUpdateAutoContinent.setText(mDomCold.getContinent());

            Objects.requireNonNull(binding.updateDomColdName.getEditText()).setText(mDomCold.getProductName());
            Objects.requireNonNull(binding.updateDomColdWeight.getEditText()).setText(mDomCold.getWeight());
            Objects.requireNonNull(binding.updateDomColdQuantityPallet.getEditText()).setText(mDomCold.getQuantityPallet());
            Objects.requireNonNull(binding.updateDomColdQuantityCarton.getEditText()).setText(mDomCold.getQuantityCarton());
            Objects.requireNonNull(binding.updateDomColdAddressReceive.getEditText()).setText(mDomCold.getAddressReceive());
            Objects.requireNonNull(binding.updateDomColdAddressDelivery.getEditText()).setText(mDomCold.getAddressDelivery());
            Objects.requireNonNull(binding.updateDomColdLength.getEditText()).setText(mDomCold.getLength());
            Objects.requireNonNull(binding.updateDomColdHeight.getEditText()).setText(mDomCold.getHeight());
            Objects.requireNonNull(binding.updateDomColdWidth.getEditText()).setText(mDomCold.getWidth());
        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_TYPE_DOM_DRY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        listStr[0] = binding.domColdUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domColdUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domColdUpdateAutoContinent.getText().toString();

        binding.domColdUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domColdUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domColdUpdateAutoContinent.setAdapter(adapterItemsContinent);

        binding.domColdUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domColdUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domColdUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomColdUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomColdCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        productName = Objects.requireNonNull(binding.updateDomColdName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomColdWeight.getEditText()).getText().toString();
        quantityPallet = Objects.requireNonNull(binding.updateDomColdQuantityPallet.getEditText()).getText().toString();
        quantityCarton = Objects.requireNonNull(binding.updateDomColdQuantityCarton.getEditText()).getText().toString();
        addressReceive = Objects.requireNonNull(binding.updateDomColdAddressReceive.getEditText()).getText().toString();
        addressDelivery = Objects.requireNonNull(binding.updateDomColdAddressDelivery.getEditText()).getText().toString();
        length = Objects.requireNonNull(binding.updateDomColdLength.getEditText()).getText().toString();
        height = Objects.requireNonNull(binding.updateDomColdWeight.getEditText()).getText().toString();
        width = Objects.requireNonNull(binding.updateDomColdWidth.getEditText()).getText().toString();
    }

    public void updateData() {
        getDataFromForm();

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

        String timeStamp = mDomCold.getpTime();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cold");
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