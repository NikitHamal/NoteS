package com.notex.create

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream

class OptionsBottomSheet(
    private val currentName: String,
    private val isNote: Boolean,
    private val listener: OptionsListener
) : BottomSheetDialogFragment() {

    interface OptionsListener {
        fun onEditSelected()
        fun onRenameSelected(newName: String)
        fun onDeleteSelected()
        fun onExportSelected()
        fun onShareSelected()
    }

    @NonNull
    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_options, null)
        dialog.setContentView(view)

        val editBtn = view.findViewById<MaterialButton>(R.id.option_edit)
        val renameBtn = view.findViewById<MaterialButton>(R.id.option_rename)
        val deleteBtn = view.findViewById<MaterialButton>(R.id.option_delete)
        val exportBtn = view.findViewById<MaterialButton>(R.id.option_export)
        val shareBtn = view.findViewById<MaterialButton>(R.id.option_share)

        // Hide edit button for notebooks (we'll use rename instead)
        editBtn.visibility = if (isNote) View.VISIBLE else View.GONE

        editBtn.setOnClickListener {
            listener.onEditSelected()
            dismiss()
        }

        renameBtn.setOnClickListener { showRenameDialog() }

        deleteBtn.setOnClickListener {
            listener.onDeleteSelected()
            dismiss()
        }

        exportBtn.setOnClickListener {
            listener.onExportSelected()
            dismiss()
        }

        shareBtn.setOnClickListener {
            listener.onShareSelected()
            dismiss()
        }

        return dialog
    }

    private fun showRenameDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Rename " + if (isNote) "Note" else "Notebook")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(currentName)
        input.selectAll()

        builder.setView(input)
        builder.setPositiveButton("Rename") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty() && newName != currentName) {
                listener.onRenameSelected(newName)
            }
            dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
