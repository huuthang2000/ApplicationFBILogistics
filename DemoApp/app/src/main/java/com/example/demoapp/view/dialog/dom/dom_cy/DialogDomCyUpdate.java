package com.example.demoapp.view.dialog.dom.dom_cy;

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
import com.example.demoapp.databinding.DialogDomCyUpdateBinding;
import com.example.demoapp.model.DomCy;
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

public class DialogDomCyUpdate extends DialogFragment {

    private DialogDomCyUpdateBinding binding;
    private DomCy domCy;

    private final String[] listStr = new String[3];

    private String stationGo, stationCome, productName, weight, quantity, etd;

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
        binding = DialogDomCyUpdateBinding.inflate(inflater, container, false);
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

    public static DialogDomCyUpdate getInstance() {
        return new DialogDomCyUpdate();
    }

    public void setData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            domCy = (DomCy) bundle.getSerializable(Constants.DOM_CY_UPDATE);

            binding.domCyUpdateAutoContainer.setText(domCy.getType());
            binding.domCyUpdateAutoMonth.setText(domCy.getMonth());
            binding.domCyUpdateAutoContinent.setText(domCy.getContinent());

            Objects.requireNonNull(binding.updateDomCyStationGo.getEditText()).setText(domCy.getStationGo());
            Objects.requireNonNull(binding.updateDomCyStationCome.getEditText()).setText(domCy.getStationCome());
            Objects.requireNonNull(binding.updateDomCyName.getEditText()).setText(domCy.getProductName());
            Objects.requireNonNull(binding.updateDomCyWeight.getEditText()).setText(domCy.getWeight());
            Objects.requireNonNull(binding.updateDomCyQuantity.getEditText()).setText(domCy.getQuantity());
            Objects.requireNonNull(binding.updateDomCyEtd.getEditText()).setText(domCy.getEtd());

        }
    }

    private void setUpViews() {

        ArrayAdapter<String> adapterItemsType = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_DOM_CY);
        ArrayAdapter<String> adapterItemsMonth = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_MONTH);
        ArrayAdapter<String> adapterItemsContinent = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, Constants.ITEMS_CONTINENT);

        binding.domCyUpdateAutoContainer.setAdapter(adapterItemsType);
        binding.domCyUpdateAutoMonth.setAdapter(adapterItemsMonth);
        binding.domCyUpdateAutoContinent.setAdapter(adapterItemsContinent);

        listStr[0] = binding.domCyUpdateAutoContainer.getText().toString();
        listStr[1] = binding.domCyUpdateAutoMonth.getText().toString();
        listStr[2] = binding.domCyUpdateAutoContinent.getText().toString();

        binding.domCyUpdateAutoContainer.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[0] = adapterView.getItemAtPosition(i).toString());

        binding.domCyUpdateAutoMonth.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[1] = adapterView.getItemAtPosition(i).toString());

        binding.domCyUpdateAutoContinent.setOnItemClickListener((adapterView, view, i, l) ->
                listStr[2] = adapterView.getItemAtPosition(i).toString());

        setCancelable(false);
    }

    public void setListenerForButtons() {
        binding.btnDomCyUpdate.setOnClickListener(view -> {
            updateData();
            dismiss();
        });
        binding.btnDomCyCancel.setOnClickListener(view -> dismiss());
    }


    public void getDataFromForm() {
        stationGo = Objects.requireNonNull(binding.updateDomCyStationGo.getEditText()).getText().toString();
        stationCome = Objects.requireNonNull(binding.updateDomCyStationCome.getEditText()).getText().toString();
        productName = Objects.requireNonNull(binding.updateDomCyName.getEditText()).getText().toString();
        weight = Objects.requireNonNull(binding.updateDomCyWeight.getEditText()).getText().toString();
        quantity = Objects.requireNonNull(binding.updateDomCyQuantity.getEditText()).getText().toString();
        etd = Objects.requireNonNull(binding.updateDomCyEtd.getEditText()).getText().toString();

    }

    public void updateData() {
        getDataFromForm();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("stationGo", stationGo);
        hashMap.put("stationCome", stationCome);
        hashMap.put("productName", productName);
        hashMap.put("weight", weight);
        hashMap.put("quantity", quantity);
        hashMap.put("etd", etd);
        hashMap.put("type", listStr[0]);
        hashMap.put("month", listStr[1]);
        hashMap.put("continent", listStr[2]);

        String timeStamp = domCy.getpTime();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dom_Cy");
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