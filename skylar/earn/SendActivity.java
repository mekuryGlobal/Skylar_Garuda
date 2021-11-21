package com.example.skylar.earn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skylar.R;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        SharedPreferences sharedPreferences = getSharedPreferences("myKeys", MODE_PRIVATE);
        String publicKey = sharedPreferences.getString("publicKey", "");
        String privateKey = sharedPreferences.getString("privateKey", "");

        String receiver = "GA2C5RFPE6GCKMY3US5PAB6UZLKIGSPIUKSLRB6Q723BM2OARMDUYEJ5";

        KeyPair source = KeyPair.fromSecretSeed(privateKey);
        KeyPair destination = KeyPair.fromAccountId(receiver);

        TextView privateKey2 = findViewById(R.id.privateKey);
        privateKey2.setText(privateKey);

        privateKey2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String copy_data = privateKey2.getText().toString();
                ClipData clipData = ClipData.newPlainText("copied", copy_data);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(SendActivity.this, "Public address copied to clipboard", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void sendStellar(KeyPair destination, KeyPair source ) throws IOException {
        String amount = " ";
        Server server = new Server("https://horizon-testnet.stellar.org");
        server.accounts().account(destination.getAccountId());
        AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
        Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), amount).build())
                .setTimeout(180)
                .setBaseFee(Transaction.MIN_BASE_FEE)
                .build();

        transaction.sign(source);

        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            String message = response.toString();
            Toast.makeText(SendActivity.this, "SUCCESS! " + message , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(SendActivity.this, "UNSUCCESSFUL!" , Toast.LENGTH_SHORT).show();

        }
    }

}