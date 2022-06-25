package com.example.demoapp.view.dialog.dom.dom_cy_sea;

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
import com.example.demoapp.databinding.DialogDomCySeaUpdateBinding;
import com.example.demoapp.model.DomCySea;
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

public class DialogDomCySeaUpdate extends DialogFragment {

    private DialogDomCySeaUpdateBinding binding;
    private DomCySea domCySea;

    private final String[] listStr = new String[3];

    private String portGo, portCome, productName, weight, quantity, etd;

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
        binding = DialogDomCySeaUpdateBinding.inflate(inflater, container, false);
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

    public static DialogDomCySeaUpdate getInstance() {
        return new DialogDomCySeaUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            domCySea = (DomCySea) bundle.getSerializable(Constants.DOM_CY_SEA_UPDATE);

            binding.domCySeaUpdateAutoContainer.setText(domCySea.getType());
            binding.domCySeaUpdateAutoMonth.setText(domCySea.getMonth());
            binding.domCySeaUpdateAutoContinent.setText(domCySea.getContinent());

            Objects.requireNonNull(binding.updateDomCySeaStationGo.getEditText()).setText(domCySea.getPortGo());
            Objects.requireNonNull(binding.updateDomCySeaStationCome.getEditText()).setText(domCySea.getPortCome());
            Objects.requireNonNull(binding.updateDomCySeaName.getEditText()).setText(domCySea.getProductName());
            Objects.requireNonNull(binding.updateDomCySeaWeight.getEditText()).setText(domCySea.getWeight());
            Objects.requireNonNull(binding.updateDomCySeaQuantity.getEditText()).setText(domCySea.getQuantity());
            Objects.requireNonNull(binding.updateDomCySeaEtd.getEditText()).setText(domCySea.getEtd());

        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM_SEA);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domCySeaUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domCySeaUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domCySeaUpdateAutoContinent.setAdapter(adapterItemsContinent);

        listStr[0] = binding.domCySeaUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domCySeaUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domCySeaUpdateAutoContinent.getText().toString();

        binding.domCySeaUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domCySeaUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domCySeaUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomCySeaUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomCySeaCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        portGo = Objects.requireNonNull(binding.updateDomCySeaStationGo.getEditText()).getText().toString();
        portCome = Objects.requireNonNull(binding.updateDomCySeaStationCome.getEditText()).getText().toString();
        productName = Objects.requireNonNull(binding.updateDomCySeaName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomCySeaWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomCySeaQuantity.getEditText()).getText().toString();
        etd = Objects.requireNonNull(binding.updateDomCySeaEtd.getEditText()).getText().toString();

    }

    public void updateData() {
        getDataFromForm();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("portGo", portGo);
        hashMap.put("portCome", portCome);
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("etd", etd);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);

        String timeStamp = domCySea.getpTime();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cy_Sea");
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