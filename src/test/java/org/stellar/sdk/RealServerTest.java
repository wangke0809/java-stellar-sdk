package org.stellar.sdk;

import org.junit.Test;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.xdr.TransactionEnvelope;
import org.stellar.sdk.xdr.XdrDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangke
 * @description: TODO
 * @date 2020/6/21 12:21 下午
 */
public class RealServerTest {

    Server server = new Server("https://horizon-testnet.stellar.org");

    @Test
    public void testSend() throws Exception{
        final KeyPair keyPair = KeyPair.fromSecretSeed("SAR34YC3PRNKJB5B7VR4OVCD4A2KQBNVZS54J6TLK5IUDUEKVXFUBKJHKE");
        String toAddress = "GCXLMSCTLEYM6X5NTBQZFF65EKJ4WMHROUL6K6IP4I3NCUCM764IFNIF";
        final String accountId = keyPair.getAccountId();
        System.out.println(accountId);

        final AccountResponse account = server.accounts().account(keyPair.getAccountId());

        Transaction.Builder builder = new Transaction.Builder(account, Network.TESTNET);

        Transaction transaction = builder.setBaseFee(100)
                .addOperation(new PaymentOperation.Builder(toAddress, new AssetTypeNative(), "1").build())
                .addMemo(Memo.text("HI")).setTimeout(3600).build();
        transaction.sign(keyPair);
        final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
        final String hash = submitTransactionResponse.getHash();
        System.out.println(hash);

    }


}
