package DSCoinPackage;

import HelperClasses.*;
import java.util.*;
public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  public int last_idx = 0;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    if(tB.dgst.charAt(0) != '0' || tB.dgst.charAt(1) != '0' || tB.dgst.charAt(2) != '0' || tB.dgst.charAt(3) != '0'){
      return false; //first 4 digits of dgst should be "0000".
    }
    String dgst;
    CRF obj = new CRF(64);
    if(tB.previous == null){
      dgst = obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce);
    }
    else{
      dgst = obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce);
    }
    if(!(dgst.equals(tB.dgst))){ //dgst computed should be correct.
      return false;
    }
    Transaction newarr[] = tB.trarray;
    MerkleTree tree = new MerkleTree();
		if(!(tB.trsummary.equals(tree.Build(newarr)))){
      return false; //the tree should have been build correctly.
    }
    int num_reward = 0;
    for(int i=0; i<newarr.length; i++){ //number of reward transactions should be <=1.
      if(newarr[i].coinsrc_block == null && newarr[i].Source == null){
        num_reward ++;
      }
      if(num_reward > 1){
        return false;
      }
      if(tB.previous != null && tB.previous.checkTransaction(newarr[i]) == false){ //checking each transaction.
        return false;
      }
    }

    for(int i = 0;i < newarr.length; i++){
      for(int j = i+1; j< newarr.length; j++){
        if(newarr[i].coinID.equals(newarr[j].coinID)){
          return false; //no double spending
        }
      }
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    ArrayList<Pair<Integer, TransactionBlock>> list = new ArrayList<Pair<Integer, TransactionBlock>>(); //this list stores the number of valid blocks in a chain, along with the block at which it ends.
    for(int i=0;i<last_idx;i++){
      TransactionBlock block = lastBlocksList[i];
      int valid = 0;
      TransactionBlock temp = block;
      while(block.previous != null){
        if(checkTransactionBlock(block) == false){
          valid = -1; //is set -1, so that it can be 0 after valid++
          temp = block.previous; //This is the last valid block, which will be pushed in arraylist of pairs.
        }
        valid++; 
        block = block.previous;
      }
      Pair<Integer, TransactionBlock> p = new Pair<Integer, TransactionBlock>(valid, temp);
      list.add(p);
    }
    int max = -1, maxBlockIdx = 0;
    for(int i=0;i<list.size();i++){
      if(list.get(i).get_first() > max){
        max = list.get(i).get_first(); //finding the max length of a valid chain from the arraylist
        maxBlockIdx = i; //the index in the list.
      }
    }

    return list.get(maxBlockIdx).get_second();
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock lastBlock = null;
    if(last_idx > 0){
      lastBlock = FindLongestValidChain();
    }
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
                          if(dgst.substring(0,4).equals("0000")){
                          newBlock.nonce = str;
                          newBlock.dgst = dgst;
                          newBlock.previous = lastBlock;
                          int idx = -1;
                          for(int i=0;i<last_idx; i++){
                            if(lastBlocksList[i] == lastBlock){
                              idx = i;
                              break;
                            }
                          }
                          if(idx != -1){ //This means that the lastBlock exists in the lastBlocksList
                            lastBlocksList[idx] = newBlock;
                          }
                          else{ //This means that lastBlock isn't a part of lastBlocksList. This is a case similar to as given in project description.
                            if(lastBlocksList == null){
                              lastBlocksList = new TransactionBlock[100];
                            }
                            lastBlocksList[last_idx] = newBlock;
                            last_idx++;
                          }
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
