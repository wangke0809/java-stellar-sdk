// Automatically generated by xdrgen
// DO NOT EDIT or your changes may be overwritten

package org.stellar.sdk.xdr;


import java.io.IOException;

import com.google.common.base.Objects;

// === xdr source ============================================================

//  struct PaymentOp
//  {
//      AccountID destination; // recipient of the payment
//      Asset asset;           // what they end up with
//      int64 amount;          // amount they end up with
//  };

//  ===========================================================================
public class PaymentOp  {
  public PaymentOp () {}
  private AccountID destination;
  public AccountID getDestination() {
    return this.destination;
  }
  public void setDestination(AccountID value) {
    this.destination = value;
  }
  private Asset asset;
  public Asset getAsset() {
    return this.asset;
  }
  public void setAsset(Asset value) {
    this.asset = value;
  }
  private Int64 amount;
  public Int64 getAmount() {
    return this.amount;
  }
  public void setAmount(Int64 value) {
    this.amount = value;
  }
  public static void encode(XdrDataOutputStream stream, PaymentOp encodedPaymentOp) throws IOException{
    AccountID.encode(stream, encodedPaymentOp.destination);
    Asset.encode(stream, encodedPaymentOp.asset);
    Int64.encode(stream, encodedPaymentOp.amount);
  }
  public static PaymentOp decode(XdrDataInputStream stream) throws IOException {
    PaymentOp decodedPaymentOp = new PaymentOp();
    decodedPaymentOp.destination = AccountID.decode(stream);
    decodedPaymentOp.asset = Asset.decode(stream);
    decodedPaymentOp.amount = Int64.decode(stream);
    return decodedPaymentOp;
  }
  @Override
  public int hashCode() {
    return Objects.hashCode(this.destination, this.asset, this.amount);
  }
  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof PaymentOp)) {
      return false;
    }

    PaymentOp other = (PaymentOp) object;
    return Objects.equal(this.destination, other.destination) && Objects.equal(this.asset, other.asset) && Objects.equal(this.amount, other.amount);
  }
}
