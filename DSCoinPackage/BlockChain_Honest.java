package DSCoinPackage;

//See chat on lab discussions channel.

import HelperClasses.*;

public class BlockChain_Honest {
  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    String str = "";
    for(int i1=49;i1 <58;i1++){
      str += (char)i1;
      for(int i2=48;i2 <58;i2++){
        str += (char)i2;
        for(int i3=48;i3 <58;i3++){
          str += (char)i3;
          for(int i4=48;i4 <58;i4++){
            str += (char)i4;
            for(int i5=48;i5 <58;i5++){
              str += (char)i5;
              for(int i6=48;i6 <58;i6++){
                str += (char)i6;
                for(int i7=48;i7 <58;i7++){
                  str += (char)i7;
                  for(int i8=48;i8 <58;i8++){
                    str += (char)i8;
                    for(int i9=48;i9 <58;i9++){
                      str += (char)i9;
                      for(int i10=48;i10 <58;i10++){
                        str += (char)i10;
                        String dgst;
                        if(lastBlock != null){
                          dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + str); 
                        }
                        else{
                          dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + str);
                        }
                          if(dgst.charAt(0) == '0' && dgst.charAt(1) == '0' && dgst.charAt(2) == '0' && dgst.charAt(3) == '0'){
                          newBlock.nonce = str;
                          newBlock.dgst = dgst;
                          newBlock.previous = lastBlock;
                          lastBlock = newBlock;
                          return;
                        }
                        str = str.substring(0,str.length()-1);
                    }
                    str = str.substring(0,str.length()-1);
                  }
                  str = str.substring(0,str.length()-1);
                 }
                 str = str.substring(0,str.length()-1);
                }
                str = str.substring(0,str.length()-1);
              }
              str = str.substring(0,str.length()-1);
            }
            str = str.substring(0,str.length()-1);
          }
          str = str.substring(0,str.length()-1);
        }
        str = str.substring(0,str.length()-1);
      }
      str = str.substring(0,str.length()-1);
    }
    return;
  }
}