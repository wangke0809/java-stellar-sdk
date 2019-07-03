package org.stellar.sdk;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StrKeyTest {
    @Test
    public void testDecodeEncode() throws IOException, FormatException {
        String seed = "SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE";
        byte[] secret = StrKey.decodeCheck(StrKey.VersionByte.SEED, seed.toCharArray());
        char[] encoded = StrKey.encodeCheck(StrKey.VersionByte.SEED, secret);
        assertEquals(seed, String.valueOf(encoded));
    }

    @Test()
    public void testDecodeInvalidVersionByte() {
        String address = "GCZHXL5HXQX5ABDM26LHYRCQZ5OJFHLOPLZX47WEBP3V2PF5AVFK2A5D";
        try {
            StrKey.decodeCheck(StrKey.VersionByte.SEED, address.toCharArray());
            fail();
        } catch (FormatException e) {}
    }

    @Test()
    public void testDecodeInvalidSeed() {
        String seed = "SAA6NXOBOXP3RXGAXBW6PGFI5BPK4ODVAWITS4VDOMN5C2M4B66ZML";
        try {
            StrKey.decodeCheck(StrKey.VersionByte.SEED, seed.toCharArray());
            fail();
        } catch (Exception e) {}
    }

    // TODO more tests
    @Test
    public void public2Accout(){
        byte[] pk = StrKey.decodeStellarAccountId("GC4L4T5ICWHTMTQ3RTCY4LVC2YHKIWUJDUFW3RX7TQ6ZWQ7QSNPJ3G5K");
        for(byte b : pk){
            System.out.println(b);
        }
    }
}
