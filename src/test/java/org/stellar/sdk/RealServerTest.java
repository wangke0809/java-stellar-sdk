package org.stellar.sdk;

import org.junit.Test;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.xdr.TransactionEnvelope;
import org.stellar.sdk.xdr.XdrDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @author wangke
 * @description: TODO
 * @date 2020/6/21 12:21 下午
 */
public class RealServerTest {

    Server server = new Server("https://gatewayohio.bitrenren.com/xlm_node/");

    @Test
    public void testGetTx() throws Exception {
        // payment
        String txHash = "6cfe93a9194e57164b9d3a4904d715b4b9f8a2c00de2fdb87f30e5b7839b69ea";
        final TransactionResponse tx = server.transactions().transaction(txHash);
        System.out.println(tx.getEnvelopeXdr());
        final byte[] bytes = Base64.getDecoder().decode(tx.getEnvelopeXdr());
//        final byte[] bytes = Base64.decodeBase64(tx.getEnvelopeXdr());
        InputStream inputStream = new ByteArrayInputStream(bytes);
        TransactionEnvelope transactionEnvelope = null;
        try {
            transactionEnvelope = TransactionEnvelope.decode(new XdrDataInputStream(inputStream));
            System.out.println(transactionEnvelope);
        } catch (IOException e) {
            System.out.println("!!!ERROR : " + e);
        }
    }

}
