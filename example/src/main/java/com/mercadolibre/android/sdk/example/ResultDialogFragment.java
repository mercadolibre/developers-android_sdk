package com.mercadolibre.android.sdk.example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.mercadolibre.android.sdk.ApiResponse;

/**
 * Simple dialog to show results
 */
public class ResultDialogFragment extends DialogFragment {


    private static final String RESPONSE_KEY = "response_key";


    public static ResultDialogFragment newInstance(ApiResponse response) {
        Bundle args = new Bundle();
        args.putParcelable(RESPONSE_KEY, response);
        ResultDialogFragment fragment = new ResultDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ResultDialogFragment() {

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ApiResponse response = getArguments().getParcelable(RESPONSE_KEY);
        View view = getActivity().getLayoutInflater().inflate(R.layout.result_dialog_fragment, null, false);
        if (response != null) {
            TextView txtCode = (TextView) view.findViewById(R.id.txt_result_code);
            TextView txtContent = (TextView) view.findViewById(R.id.txt_result_message);

            txtCode.setText(getString(R.string.result_code, response.getResponseCode()));
            String content = response.getContent();
            if(content == null && response.getErrorException() != null) {
                content = response.getErrorException().getMessage();
            }
            txtContent.setText(getString(R.string.result_content, content));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.result_title);

        return builder.create();
    }
}
