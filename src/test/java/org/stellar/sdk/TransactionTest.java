package org.stellar.sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.stellar.sdk.xdr.XdrDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class TransactionTest {

  @BeforeEach
  public void setupNetwork() {
    Network.useTestNetwork();
  }

  @Test
  public void testDefaultBaseFee() throws FormatException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Transaction.Builder.setDefaultOperationFee(2345);

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    transaction.sign(source);

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAJKQAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAEqBfIAAAAAAAAAAABtoeVYgAAAEDf8XAGz9uOmfL0KJBP29eSXz/CZqZtl0Mm8jHye3xwLgo2HJDfvCJdijGKsx34AfNl6hvX+Cq3IVk062sLSuoK",
            transaction.toEnvelopeXdrBase64());

    Transaction transaction2 = Transaction.fromEnvelopeXdr(transaction.toEnvelopeXdr());

    assertEquals(transaction.getSourceAccount().getAccountId(), transaction2.getSourceAccount().getAccountId());
    assertEquals(transaction.getSequenceNumber(), transaction2.getSequenceNumber());
    assertEquals(transaction.getFee(), transaction2.getFee());
    assertEquals(
            ((CreateAccountOperation)transaction.getOperations()[0]).getStartingBalance(),
            ((CreateAccountOperation)transaction2.getOperations()[0]).getStartingBalance()
    );
  }

  @Test
  public void testDefaultBaseFeeThrows() {
    Exception e = assertThrows(RuntimeException.class, () -> Transaction.Builder.setDefaultOperationFee(99));
    assertThat(
            e.getMessage(),
            containsString("DefaultOperationFee cannot be smaller than the BASE_FEE")
    );


    // should succeed
    Transaction.Builder.setDefaultOperationFee(100);
  }

  @Test
  public void testBuilderSuccessTestnet() throws FormatException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    long sequenceNumber = 2908908335136768L;
    Account account = new Account(source, sequenceNumber);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    transaction.sign(source);

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAZAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAEqBfIAAAAAAAAAAABtoeVYgAAAEDLki9Oi700N60Lo8gUmEFHbKvYG4QSqXiLIt9T0ru2O5BphVl/jR9tYtHAD+UeDYhgXNgwUxqTEu1WukvEyYcD",
            transaction.toEnvelopeXdrBase64());

    assertEquals(transaction.getSourceAccount(), source);
    assertEquals(transaction.getSequenceNumber(), sequenceNumber+1);
    assertEquals(transaction.getFee(), 100);

    Transaction transaction2 = Transaction.fromEnvelopeXdr(transaction.toEnvelopeXdr());

    assertEquals(transaction.getSourceAccount().getAccountId(), transaction2.getSourceAccount().getAccountId());
    assertEquals(transaction.getSequenceNumber(), transaction2.getSequenceNumber());
    assertEquals(transaction.getFee(), transaction2.getFee());
    assertEquals(
            ((CreateAccountOperation)transaction.getOperations()[0]).getStartingBalance(),
            ((CreateAccountOperation)transaction2.getOperations()[0]).getStartingBalance()
    );

    assertEquals(transaction.getSignatures(), transaction2.getSignatures());
  }

  @Test
  public void testBuilderMemoText() throws FormatException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .addMemo(Memo.text("Hello world!"))
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    transaction.sign(source);

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAZAAKVaMAAAABAAAAAAAAAAEAAAAMSGVsbG8gd29ybGQhAAAAAQAAAAAAAAAAAAAAAO3gUmG83C+VCqO6FztuMtXJF/l7grZA7MjRzqdZ9W8QAAAABKgXyAAAAAAAAAAAAbaHlWIAAABAxzofBhoayuUnz8t0T1UNWrTgmJ+lCh9KaeOGu2ppNOz9UGw0abGLhv+9oWQsstaHx6YjwWxL+8GBvwBUVWRlBQ==",
            transaction.toEnvelopeXdrBase64());

    Transaction transaction2 = Transaction.fromEnvelopeXdr(transaction.toEnvelopeXdr());

    assertEquals(transaction.getSourceAccount().getAccountId(), transaction2.getSourceAccount().getAccountId());
    assertEquals(transaction.getSequenceNumber(), transaction2.getSequenceNumber());
    assertEquals(transaction.getMemo(), transaction2.getMemo());
    assertEquals(transaction.getFee(), transaction2.getFee());
    assertEquals(
            ((CreateAccountOperation)transaction.getOperations()[0]).getStartingBalance(),
            ((CreateAccountOperation)transaction2.getOperations()[0]).getStartingBalance()
    );
  }
  
  @Test
  public void testBuilderTimeBounds() throws FormatException, IOException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .addTimeBounds(new TimeBounds(42, 1337))
            .addMemo(Memo.hash("abcdef"))
            .build();

    transaction.sign(source);
    
    // Convert transaction to binary XDR and back again to make sure timebounds are correctly de/serialized.
    XdrDataInputStream is = new XdrDataInputStream(
    		new ByteArrayInputStream(
    				javax.xml.bind.DatatypeConverter.parseBase64Binary(transaction.toEnvelopeXdrBase64())
    		)
    );
    org.stellar.sdk.xdr.Transaction decodedTransaction = org.stellar.sdk.xdr.Transaction.decode(is);

    assertEquals(decodedTransaction.getTimeBounds().getMinTime().getTimePoint().getUint64().longValue(), 42);
    assertEquals(decodedTransaction.getTimeBounds().getMaxTime().getTimePoint().getUint64().longValue(), 1337);

    Transaction transaction2 = Transaction.fromEnvelopeXdr(transaction.toEnvelopeXdr());

    assertEquals(transaction.getSourceAccount().getAccountId(), transaction2.getSourceAccount().getAccountId());
    assertEquals(transaction.getSequenceNumber(), transaction2.getSequenceNumber());
    assertEquals(transaction.getMemo(), transaction2.getMemo());
    assertEquals(transaction.getTimeBounds(), transaction2.getTimeBounds());
    assertEquals(transaction.getFee(), transaction2.getFee());
    assertEquals(
            ((CreateAccountOperation)transaction.getOperations()[0]).getStartingBalance(),
            ((CreateAccountOperation)transaction2.getOperations()[0]).getStartingBalance()
    );
  }

  @Test
  public void testBuilderBaseFee() throws FormatException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .setOperationFee(200)
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    transaction.sign(source);

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAyAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAEqBfIAAAAAAAAAAABtoeVYgAAAED1Mbd0oou0sfNFRuxsSqvrwJ9RzzTSnX8sTmlbMKcV0V3Kl1eDKoerD+xZ1pNQwOJZrAG2yapXyg60PQfDUcMN",
            transaction.toEnvelopeXdrBase64());

    Transaction transaction2 = Transaction.fromEnvelopeXdr(transaction.toEnvelopeXdr());

    assertEquals(transaction.getSourceAccount().getAccountId(), transaction2.getSourceAccount().getAccountId());
    assertEquals(transaction.getSequenceNumber(), transaction2.getSequenceNumber());
    assertEquals(transaction.getFee(), transaction2.getFee());
    assertEquals(
            ((CreateAccountOperation)transaction.getOperations()[0]).getStartingBalance(),
            ((CreateAccountOperation)transaction2.getOperations()[0]).getStartingBalance()
    );
  }

  @Test
  public void testBuilderBaseFeeThrows() throws FormatException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");

    Account account = new Account(source, 2908908335136768L);

    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account).setOperationFee(99));
    assertThat(
            e.getMessage(),
            containsString("OperationFee cannot be smaller than the BASE_FEE")
    );
  }

  @Test
  public void testBuilderWithTimeBoundsButNoTimeout() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
            .addTimeBounds(new TimeBounds(42, 1337))
            .addMemo(Memo.hash("abcdef"))
            .build();
  }

  @Test
  public void testBuilderRequiresTimeoutOrTimeBounds() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
            .addMemo(Memo.hash("abcdef"))
            .build());
    assertEquals(e.getMessage(), "TimeBounds has to be set or you must call setTimeout(TIMEOUT_INFINITE).");
  }


  @Test
  public void testBuilderTimeoutNegative() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
            .addMemo(Memo.hash("abcdef"))
            .setTimeout(-1)
            .build());
    assertEquals(e.getMessage(), "timeout cannot be negative");
  }

  @Test
  public void testBuilderTimeoutSetsTimeBounds() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
            .setTimeout(10)
            .build();

    assertEquals(0, transaction.getTimeBounds().getMinTime());
    long currentUnix = System.currentTimeMillis() / 1000L;
    assertEquals(currentUnix + 10, transaction.getTimeBounds().getMaxTime());
  }

  @Test
  public void testBuilderFailsWhenSettingTimeoutAndMaxTimeAlreadySet() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
            .addTimeBounds(new TimeBounds(42, 1337))
            .setTimeout(10)
            .build());
    assertEquals(
            e.getMessage(),
            "TimeBounds.max_time has been already set - setting timeout would overwrite it."
    );
  }

  @Test
  public void testBuilderFailsWhenSettingTimeoutAndMaxTimeNotSet() throws IOException {
    Account account = new Account(KeyPair.random(), 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
              .addOperation(new CreateAccountOperation.Builder(KeyPair.random(), "2000").build())
              .addTimeBounds(new TimeBounds(42, 0))
              .setTimeout(10)
              .build();

    assertEquals(42, transaction.getTimeBounds().getMinTime());
    // Should add max_time
    long currentUnix = System.currentTimeMillis() / 1000L;
    assertEquals(currentUnix + 10, transaction.getTimeBounds().getMaxTime());
  }

  @Test
  public void testBuilderTimeBoundsNoMaxTime() throws FormatException, IOException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .addTimeBounds(new TimeBounds(42, 0))
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .addMemo(Memo.hash("abcdef"))
            .setOperationFee(100)
            .build();

    transaction.sign(source);

    // Convert transaction to binary XDR and back again to make sure timebounds are correctly de/serialized.
    XdrDataInputStream is = new XdrDataInputStream(
            new ByteArrayInputStream(
                    javax.xml.bind.DatatypeConverter.parseBase64Binary(transaction.toEnvelopeXdrBase64())
            )
    );
    org.stellar.sdk.xdr.Transaction decodedTransaction = org.stellar.sdk.xdr.Transaction.decode(is);

    assertEquals(decodedTransaction.getTimeBounds().getMinTime().getTimePoint().getUint64().longValue(), 42);
    assertEquals(decodedTransaction.getTimeBounds().getMaxTime().getTimePoint().getUint64().longValue(), 0);
  }

  @Test
  public void testBuilderSuccessPublic() throws FormatException {
    Network.usePublicNetwork();

    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    transaction.sign(source);

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAZAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAEqBfIAAAAAAAAAAABtoeVYgAAAEDzfR5PgRFim5Wdvq9ImdZNWGBxBWwYkQPa9l5iiBdtPLzAZv6qj+iOfSrqinsoF0XrLkwdIcZQVtp3VRHhRoUE",
            transaction.toEnvelopeXdrBase64());
  }

  @Test
  public void testSha256HashSigning() throws FormatException {
    Network.usePublicNetwork();

    KeyPair source = KeyPair.fromAccountId("GBBM6BKZPEHWYO3E3YKREDPQXMS4VK35YLNU7NFBRI26RAN7GI5POFBB");
    KeyPair destination = KeyPair.fromAccountId("GDJJRRMBK4IWLEPJGIE6SXD2LP7REGZODU7WDC3I2D6MR37F4XSHBKX2");

    Account account = new Account(source, 0L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "2000").build())
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    byte[] preimage = new byte[64];
    new SecureRandom().nextBytes(preimage);
    byte[] hash = Util.hash(preimage);

    transaction.sign(preimage);

    assertArrayEquals(transaction.getSignatures().get(0).getSignature().getSignature(), preimage);
    assertArrayEquals(
            transaction.getSignatures().get(0).getHint().getSignatureHint(),
            Arrays.copyOfRange(hash, hash.length - 4, hash.length)
    );
  }

  @Test
  public void testToBase64EnvelopeXdrBuilderNoSignatures() throws FormatException, IOException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Transaction transaction = new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .setTimeout(Transaction.Builder.TIMEOUT_INFINITE)
            .build();

    assertEquals(
            "AAAAAF7FIiDToW1fOYUFBC0dmyufJbFTOa2GQESGz+S2h5ViAAAAZAAKVaMAAAABAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAA7eBSYbzcL5UKo7oXO24y1ckX+XuCtkDsyNHOp1n1bxAAAAAEqBfIAAAAAAAAAAAA",
            transaction.toEnvelopeXdrBase64()
    );
  }

  @Test
  public void testNoOperations() throws FormatException, IOException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");

    Account account = new Account(source, 2908908335136768L);
    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account).
            setTimeout(Transaction.Builder.TIMEOUT_INFINITE).build());
    assertEquals(e.getMessage(), "At least one operation required");
  }

  @Test
  public void testTryingToAddMemoTwice() throws FormatException, IOException {
    // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
    KeyPair source = KeyPair.fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS");
    KeyPair destination = KeyPair.fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR");

    Account account = new Account(source, 2908908335136768L);
    Exception e = assertThrows(RuntimeException.class, () -> new Transaction.Builder(account)
            .addOperation(new CreateAccountOperation.Builder(destination, "2000").build())
            .addMemo(Memo.none())
            .addMemo(Memo.none())
            .build());
    assertEquals(e.getMessage(), "Memo has been already added.");
  }
}