package com.notex.create;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.cardview.widget.CardView;

public class OptionsBottomSheet extends BottomSheetDialogFragment {
    public interface OptionsListener {
        void onEditSelected();
        void onRenameSelected(String newName);
        void onDeleteSelected();
        void onExportSelected();
        void onShareSelected();
    }

    private OptionsListener listener;
    private String currentName;
    private boolean isNote;

    public OptionsBottomSheet(String currentName, boolean isNote, OptionsListener listener) {
        this.currentName = currentName;
        this.isNote = isNote;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_options, null);
        dialog.setContentView(view);

        MaterialButton editBtn = view.findViewById(R.id.option_edit);
        MaterialButton renameBtn = view.findViewById(R.id.option_rename);
        MaterialButton deleteBtn = view.findViewById(R.id.option_delete);
        MaterialButton exportBtn = view.findViewById(R.id.option_export);
        MaterialButton shareBtn = view.findViewById(R.id.option_share);

        // Hide edit button for notebooks (we'll use rename instead)
        editBtn.setVisibility(isNote ? View.VISIBLE : View.GONE);

        editBtn.setOnClickListener(v -> {
            listener.onEditSelected();
            dismiss();
        });

        renameBtn.setOnClickListener(v -> showRenameDialog());

        deleteBtn.setOnClickListener(v -> {
            listener.onDeleteSelected();
            dismiss();
        });

        exportBtn.setOnClickListener(v -> {
            listener.onExportSelected();
            dismiss();
        });

        shareBtn.setOnClickListener(v -> {
            listener.onShareSelected();
            dismiss();
        });

        return dialog;
    }

    private void showRenameDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Rename " + (isNote ? "Note" : "Notebook"));
        
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentName);
        input.selectAll();
        
        builder.setView(input);
        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(currentName)) {
                listener.onRenameSelected(newName);
            }
            dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}