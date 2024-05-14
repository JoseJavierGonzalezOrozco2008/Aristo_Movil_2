package com.example.aristomovil2.servicio;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumeroTelefonicoFormat implements TextWatcher {

    private EditText editText;
    private boolean formato;


    public NumeroTelefonicoFormat(EditText editText) {
        this.editText = editText;
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (formato){
            return;
        }

        formato = true;

        String numCel = s.toString().replaceAll("[^\\d]", "");

        StringBuilder formatoNum = new StringBuilder();
        
        for (int i = 0; i < numCel.length(); i++) {
            if (i == 0) {
                formatoNum.append("(").append(numCel.charAt(i));
            } else if (i == 3) {
                formatoNum.append(")-").append(numCel.charAt(i));
            } else if (i == 6 || i == 10) {
                formatoNum.append("-").append(numCel.charAt(i));
            } else {
                formatoNum.append(numCel.charAt(i));
            }
        }

        editText.removeTextChangedListener(this);
        editText.setText(formatoNum.toString());
        editText.setSelection(formatoNum.length());
        editText.addTextChangedListener(this);

        formato = false;
    }
}
