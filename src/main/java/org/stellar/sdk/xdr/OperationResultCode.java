// Automatically generated by xdrgen
// DO NOT EDIT or your changes may be overwritten

package org.stellar.sdk.xdr;


import java.io.IOException;


// === xdr source ============================================================

//  enum OperationResultCode
//  {
//      opINNER = 0, // inner object result is valid
//  
//      opBAD_AUTH = -1,            // too few valid signatures / wrong network
//      opNO_ACCOUNT = -2,          // source account was not found
//      opNOT_SUPPORTED = -3,       // operation not supported at this time
//      opTOO_MANY_SUBENTRIES = -4, // max number of subentries already reached
//      opEXCEEDED_WORK_LIMIT = -5  // operation did too much work
//  };

//  ===========================================================================
public enum OperationResultCode implements XdrElement {
  opINNER(0),
  opBAD_AUTH(-1),
  opNO_ACCOUNT(-2),
  opNOT_SUPPORTED(-3),
  opTOO_MANY_SUBENTRIES(-4),
  opEXCEEDED_WORK_LIMIT(-5),
  ;
  private int mValue;

  OperationResultCode(int value) {
      mValue = value;
  }

  public int getValue() {
      return mValue;
  }

  public static OperationResultCode decode(XdrDataInputStream stream) throws IOException {
    int value = stream.readInt();
    switch (value) {
      case 0: return opINNER;
      case -1: return opBAD_AUTH;
      case -2: return opNO_ACCOUNT;
      case -3: return opNOT_SUPPORTED;
      case -4: return opTOO_MANY_SUBENTRIES;
      case -5: return opEXCEEDED_WORK_LIMIT;
      default:
        throw new RuntimeException("Unknown enum value: " + value);
    }
  }

  public static void encode(XdrDataOutputStream stream, OperationResultCode value) throws IOException {
    stream.writeInt(value.getValue());
  }

  public void encode(XdrDataOutputStream stream) throws IOException {
    encode(stream, this);
  }
}
