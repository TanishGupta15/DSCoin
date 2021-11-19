package DSCoinPackage;

import java.util.*;
import HelperClasses.*;
public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins; //Invariant : mycoins list is always sorted with respect to coinID
  public Transaction[] in_process_trans;
  public int last_idx; //Stores the last non-null index of in_process_trans array.

  public void initiateCoinsend(String destUID, DSCoin_Honest DSObj) {
    Pair<String, TransactionBlock> pair = mycoins.get(0);
    mycoins.remove(0); //Since we always spend the coin with least coinID
    Transaction tobj = new Transaction();
    tobj.coinID = pair.get_first(); //coinID of coin to be transacted
    tobj.Source = this; 
    Members dest = new Members();
    for(int i=0;i<DSObj.memberlist.length;i++) {
      if(DSObj.memberlist[i].UID.equals(destUID)){
        dest = DSObj.memberlist[i]; //searching for the Members "object" with same UID as given
        break;
      }
    }
    tobj.Destination = dest;
    tobj.coinsrc_block = pair.get_second(); //coinsrc_block of the coin to be transacted
    if(in_process_trans == null) {
      in_process_trans = new Transaction[100];
    }
    
    in_process_trans[last_idx] = tobj; //filling up the last non-null index of in_process_trans with the given trans
    last_idx++;

    DSObj.pendingTransactions.AddTransactions(tobj); //Enqueue this transaction to the global TransactionQueue

    return;
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    try {
      BlockChain_Honest chain = DSObj.bChain;
      TransactionBlock block = chain.lastBlock;
      int found = 0, idx = 0;
      while(block != null && found == 0){
        for(int i = 0 ;  i<block.trarray.length ; i++){
          if(block.trarray[i].coinID.equals(tobj.coinID) && block.trarray[i].Source.UID.equals(tobj.Source.UID)){ //Searching for the given Transaction
            found = 1;
            idx = i;
            break;
          }
        }
        if(found == 0){
          block = block.previous;
        }
      }
      if(found == 0){ //given transaction was not found in any block
        throw new MissingTransactionException();
      }

      int flag = 0;
      for(int j = 0;j<tobj.Destination.mycoins.size();j++){
        if(Integer.parseInt(tobj.Destination.mycoins.get(j).get_first()) > Integer.parseInt(tobj.coinID)){
          tobj.Destination.mycoins.add(j,new Pair<String, TransactionBlock>(tobj.coinID, block)); //Maintaining the invariant that mycoins list is always sorted
          flag = 1;
          break;
        }
      }
      if(flag == 0){
        tobj.Destination.mycoins.add(new Pair<String, TransactionBlock>(tobj.coinID, block));
      }

      List<Pair<String, String>> l1 = block.Tree.getPath(idx); //Getting sibling coupled path to root of the given transaction
      List<Pair<String,String>> l2 = new ArrayList<Pair<String,String>>(); //for storing the 2nd list as expected in output
      l2.add(new Pair<String,String>(block.previous.dgst, null)); //1st element of the 2nd list
      TransactionBlock traverse = chain.lastBlock;
      List<Pair<String,String>> temp = new ArrayList<Pair<String,String>>(); //making a temporary list, will finally reverse its order to add in l2.
      while(traverse != block.previous){
        Pair<String,String> p = new Pair<String,String>(traverse.dgst, traverse.previous.dgst + "#" + traverse.trsummary + "#" + traverse.nonce);
        temp.add(p);  
        if(traverse.previous == null){ 
          break;
        }
        traverse = traverse.previous;
      }
      for(int i = temp.size() - 1;i>=0;i--){ //Reversing temp and adding in l2.
        l2.add(temp.get(i));
      }
      idx = 0;
      for(int i = 0 ; i < last_idx ; i++){ //removing the given transaction from in_process_trans array of the member.
        if(in_process_trans[i].coinID.equals(tobj.coinID) && in_process_trans[i].Source == tobj.Source && in_process_trans[i].Destination == tobj.Destination){
          idx = i;
          break;
        }
      }

      for(int i = idx; i<last_idx; i++) {
        in_process_trans[i] = in_process_trans[i+1];
      }

      last_idx --;

      Pair<List<Pair<String, String>>, List<Pair<String, String>>> ans = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(l1,l2);
      return ans;
    

    } catch (MissingTransactionException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void MineCoin(DSCoin_Honest DSObj){
    try {
      ArrayList<Transaction> txs = new ArrayList<Transaction>();
      TransactionQueue txq = DSObj.pendingTransactions;
      TransactionBlock block = DSObj.bChain.lastBlock;
      while(txs.size() != DSObj.bChain.tr_count - 1){
        Transaction tx = txq.RemoveTransaction();
        if(block.checkTransaction(tx) == true){  //checking if the transaction is valid
          int dont = 0;
          for(int i = 0 ; i < txs.size() ; i++){  //checking for double spending
            if(txs.get(i).coinID.equals(tx.coinID)){
              dont = 1;
              break;
            }
          }
          if(dont == 0){
            txs.add(tx);
          }
        }
      }
      Transaction extra = new Transaction(); //Reward transaction
      DSObj.latestCoinID = Integer.toString(Integer.valueOf(DSObj.latestCoinID) + 1);
      extra.coinID = DSObj.latestCoinID;
      extra.Source = null;
      extra.Destination = this;
      extra.coinsrc_block = null;
      txs.add(extra);
      
      for(int i = 0;i<txs.size() - 1;i++){
        txs.get(i).next = txs.get(i+1);
      }

      Transaction trans_array[] = new Transaction[txs.size()];
      for(int i = 0;i<txs.size();i++){
        trans_array[i] = txs.get(i);
      }
      TransactionBlock tB = new TransactionBlock(trans_array);
      mycoins.add(new Pair<String,TransactionBlock>(trans_array[trans_array.length - 1].coinID, tB)); //Adding reward coin to the mycoins list of miner
      DSObj.bChain.InsertBlock_Honest(tB); //insert newly created block in blockchain
      return;
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    try {
      ArrayList<Transaction> txs = new ArrayList<Transaction>();
      TransactionQueue txq = DSObj.pendingTransactions;
      TransactionBlock block = DSObj.bChain.FindLongestValidChain();
      while(txs.size() != DSObj.bChain.tr_count - 1){
        Transaction tx = txq.RemoveTransaction();
        if(block.checkTransaction(tx) == true){ //checking if the transaction is valid
          int dont = 0;
          for(int i = 0 ; i < txs.size() ; i++){  //checking for double spending
            if(txs.get(i).coinID.equals(tx.coinID)){
              dont = 1;
              break;
            }
          }
          if(dont == 0){
            txs.add(tx);
          }
        }
      }
      Transaction extra = new Transaction(); //Reward transaction
      DSObj.latestCoinID = Integer.toString(Integer.valueOf(DSObj.latestCoinID) + 1);
      extra.coinID = DSObj.latestCoinID;
      extra.Source = null;
      extra.Destination = this;
      extra.coinsrc_block = null;
      txs.add(extra);
      
      for(int i = 0;i<txs.size() - 1;i++){
        txs.get(i).next = txs.get(i+1);
      }

      Transaction trans_array[] = new Transaction[txs.size()];
      for(int i = 0;i<txs.size();i++){
        trans_array[i] = txs.get(i);
      }

      TransactionBlock tB = new TransactionBlock(trans_array);
      mycoins.add(new Pair<String,TransactionBlock>(trans_array[trans_array.length - 1].coinID, tB)); //Adding reward coin to the mycoins list of miner
      DSObj.bChain.InsertBlock_Malicious(tB); //insert newly created block in blockchain
      return;
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  
}
