package org.stellar.sdk;

import com.google.common.io.BaseEncoding;
import org.stellar.sdk.xdr.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractTransaction {
  protected final Network mNetwork;
  protected List<DecoratedSignature> mSignatures;
  public static final int MIN_BASE_FEE = 100;


  AbstractTransaction(Network network) {
    this.mNetwork = checkNotNull(network, "network cannot be null");
    this.mSignatures = new ArrayList<DecoratedSignature>();
  }

  /**
   * Adds a new signature ed25519PublicKey to this transaction.
   * @param signer {@link KeyPair} object representing a signer
   */
  public void sign(KeyPair signer) {
    checkNotNull(signer, "signer cannot be null");
    byte[] txHash = this.hash();
    mSignatures.add(signer.signDecorated(txHash));
  }

  /**
   * Adds a new signature ed25519PublicKey to this transaction.
   * @param signer {@link KeyPair} object representing a signer
   * add by pizi
   */
  public void sign(KeyPair signer, byte[] sig) {
    checkNotNull(signer, "signer cannot be null");
    mSignatures.add(signer.getDecoratedSignature(sig));
  }

  /**
   * Adds a new sha256Hash signature to this transaction by revealing preimage.
   * @param preimage the sha256 hash of preimage should be equal to signer hash
   */
  public void sign(byte[] preimage) {
    checkNotNull(preimage, "preimage cannot be null");
    org.stellar.sdk.xdr.Signature signature = new org.stellar.sdk.xdr.Signature();
    signature.setSignature(preimage);

    byte[] hash = Util.hash(preimage);
    byte[] signatureHintBytes = Arrays.copyOfRange(hash, hash.length - 4, hash.length);
    SignatureHint signatureHint = new SignatureHint();
    signatureHint.setSignatureHint(signatureHintBytes);

    DecoratedSignature decoratedSignature = new DecoratedSignature();
    decoratedSignature.setHint(signatureHint);
    decoratedSignature.setSignature(signature);

    mSignatures.add(decoratedSignature);
  }

  /**
   * Returns transaction hash.
   */
  public byte[] hash() {
    return Util.hash(this.signatureBase());
  }

  /**
   * Returns transaction hash encoded as a hexadecimal string.
   */
  public String hashHex() {
    return BaseEncoding.base16().lowerCase().encode(this.hash());
  }

  /**
   * Returns signature base.
   */
  public abstract byte[] signatureBase();

  public Network getNetwork() {
    return mNetwork;
  }

  public List<DecoratedSignature> getSignatures() {
    return mSignatures;
  }

  public abstract TransactionEnvelope toEnvelopeXdr();

  /**
   * Returns base64-encoded TransactionEnvelope XDR object. Transaction need to have at least one signature.
   */
  public String toEnvelopeXdrBase64() {
    try {
      TransactionEnvelope envelope = this.toEnvelopeXdr();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      XdrDataOutputStream xdrOutputStream = new XdrDataOutputStream(outputStream);
      TransactionEnvelope.encode(xdrOutputStream, envelope);

      BaseEncoding base64Encoding = BaseEncoding.base64();
      return base64Encoding.encode(outputStream.toByteArray());
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * Creates a <code>AbstractTransaction</code> instance from previously build <code>TransactionEnvelope</code>
   * @param envelope
   * @return
   */
  public static AbstractTransaction fromEnvelopeXdr(TransactionEnvelope envelope, Network network) {
    switch (envelope.getDiscriminant()) {
      case ENVELOPE_TYPE_TX:
        return Transaction.fromV1EnvelopeXdr(envelope.getV1(), network);
      case ENVELOPE_TYPE_TX_V0:
        return Transaction.fromV0EnvelopeXdr(envelope.getV0(), network);
      case ENVELOPE_TYPE_TX_FEE_BUMP:
        return FeeBumpTransaction.fromFeeBumpTransactionEnvelope(envelope.getFeeBump(), network);
      default:
        throw new IllegalArgumentException("transaction type is not supported: "+envelope.getDiscriminant());
    }
  }

  /**
   * Creates a <code>Transaction</code> instance from previously build <code>TransactionEnvelope</code>
   * @param envelope Base-64 encoded <code>TransactionEnvelope</code>
   * @return
   * @throws IOException
   */
  public static AbstractTransaction fromEnvelopeXdr(String envelope, Network network) throws IOException {
    BaseEncoding base64Encoding = BaseEncoding.base64();
    byte[] bytes = base64Encoding.decode(envelope);

    TransactionEnvelope transactionEnvelope = TransactionEnvelope.decode(new XdrDataInputStream(new ByteArrayInputStream(bytes)));
    return fromEnvelopeXdr(transactionEnvelope, network);
  }
}
