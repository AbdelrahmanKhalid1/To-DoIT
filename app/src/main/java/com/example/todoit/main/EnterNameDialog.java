package com.example.todoit.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.todoit.R;

import java.util.Objects;

public class EnterNameDialog extends AppCompatDialogFragment {

    private EditText username;
    private EnterNameDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog_enter_name, null);
        username = view.findViewById(R.id.editText_enter_username);
        builder.setView(view)
                .setTitle(getString(R.string.enter_your_name))
                .setPositiveButton("ok", null);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EnterNameDialogListener) context;
        } catch (ClassCastException ignore) {
            throw new ClassCastException(context.toString() + "must be implemented EnterNameDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) Objects.requireNonNull(getDialog())).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                if(!name.isEmpty()){
                    listener.onEnterUserName(username.getText().toString());
                    dismiss();
                }else {
                    Toast.makeText(getContext(), "Enter Your Name Please", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface EnterNameDialogListener {
        void onEnterUserName(String username);
    }
}
