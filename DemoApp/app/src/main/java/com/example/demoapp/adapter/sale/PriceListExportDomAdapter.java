package com.example.demoapp.adapter.sale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.databinding.RowDomExportBinding;
import com.example.demoapp.model.DomExport;
import com.example.demoapp.utilities.Constants;
import com.example.demoapp.view.dialog.dom.dom_export.sale.DialogDomExportSaleDetail;

import java.util.List;

public class PriceListExportDomAdapter extends RecyclerView.Adapter<PriceListExportDomAdapter.ExportViewHolder> {
    private final Context context;
    private List<DomExport> listExport;

    @NonNull
    @Override
    public ExportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExportViewHolder(RowDomExportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExportViewHolder holder, int  position) {

        DomExport domExport = listExport.get(position);
        holder.bind(domExport);

        holder.binding.rowCvDomExport.setOnClickListener(view -> goToDetail(domExport));

    }

    public void goToDetail(DomExport domExport){
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();
        DialogFragment dialogFragment = DialogDomExportSaleDetail.getInstance();

        Bundle bundle = new Bundle();

        bundle.putSerializable(Constants.DOM_EXPORT_OBJECT, domExport);

        dialogFragment.setArguments(bundle);
        dialogFragment.show( fm,"Detail Dom Export");
    }

    public PriceListExportDomAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDomExport(List<DomExport> list) {
        this.listExport = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (listExport != null) {
            return listExport.size();
        }
        return 0;
    }

    public void filterList(List<DomExport> filteredList) {
        listExport = filteredList;
        notifyDataSetChanged();
    }

    public static class ExportViewHolder extends RecyclerView.ViewHolder {
        private final RowDomExportBinding binding;

        public ExportViewHolder(@NonNull RowDomExportBinding root) {
            super(root.getRoot());
            binding = root;
        }

        public void bind(DomExport export) {
            binding.tvDomExportProductStt.setText(export.getStt());
            binding.tvDomExportProductName.setText(export.getProductName());
            binding.tvDomExportWeight.setText(export.getWeight());
            binding.tvDomExportQuantity.setText(export.getQuantity());
            binding.tvDomExportTemp.setText(export.getTemp());
            binding.tvDomExportAddress.setText(export.getAddress());
            binding.tvDomExportSeaport.setText(export.getPortExport());
            binding.tvDomExportLength.setText(export.getLength());
            binding.tvRowDomExportHeight.setText(export.getHeight());
            binding.tvRowDomExportWidth.setText(export.getWidth());
        }
    }
}
