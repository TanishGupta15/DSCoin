package DSCoinPackage;

import java.util.*;
import HelperClasses.*;
public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount){

    if(DSObj.pendingTransactions == null){
      DSObj.pendingTransactions = new TransactionQueue();
    }

   try {    
     Members mod = new Members();
     mod.UID = "Moderator"; //Creating a new Members with UID Moderator.
     for(int i=0;i<coinCount;i++) {
       Transaction tobj = new Transaction();
       tobj.Source = mod;
       tobj.Destination = DSObj.memberlist[i%(DSObj.memberlist.length)]; //Distributing #coinCount coins in round-robin fashion
       tobj.coinsrc_block = null;
       tobj.coinID = Integer.toString(100000 + i);
       DSObj.pendingTransactions.AddTransactions(tobj);
     }
     DSObj.latestCoinID = Integer.toString(100000 + coinCount - 1); // Invariant : latestCoin ID is the ID of last allotted coin.
     while(DSObj.pendingTransactions.size() != 0){
       Transaction t[] = new Transaction[DSObj.bChain.tr_count];
       for(int i=0;i<DSObj.bChain.tr_count;i++){
         t[i] = DSObj.pendingTransactions.RemoveTransaction();
       }
       TransactionBlock tB = new TransactionBlock(t); //Building blocks of tr_count transactions
       for(int i=0;i<t.length;i++){
        if(t[i].Destination.mycoins == null){
          t[i].Destination.mycoins = new ArrayList<Pair<String,TransactionBlock>>();
        }
         t[i].Destination.mycoins.add(new Pair<String, TransactionBlock>(t[i].coinID, tB)); //Adding coins to the mycoinslist of each members
       }
       DSObj.bChain.InsertBlock_Honest(tB); //Inserting the newly created block in blockchain
     }
   } catch (Exception e) {
    e.printStackTrace();
   }

  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {

    if(DSObj.pendingTransactions == null){
      DSObj.pendingTransactions = new TransactionQueue();
    }

    try {    
      Members mod = new Members();
      mod.UID = "Moderator";//Creating a new Members with UID Moderator.
      for(int i=0;i<coinCount;i++) {
        Transaction tobj = new Transaction();
        tobj.Source = mod;
        tobj.Destination = DSObj.memberlist[i%(DSObj.memberlist.length)]; //Distributing #coinCount coins in round-robin fashion
        tobj.coinsrc_block = null;
        tobj.coinID = Integer.toString(100000 + i);
        DSObj.pendingTransactions.AddTransactions(tobj);
      }
      DSObj.latestCoinID = Integer.toString(100000 + coinCount - 1); // Invariant : latestCoin ID is the ID of last allotted coin.
      while(DSObj.pendingTransactions.size() != 0){
        Transaction t[] = new Transaction[DSObj.bChain.tr_count];
        for(int i=0;i<DSObj.bChain.tr_count;i++){
          t[i] = DSObj.pendingTransactions.RemoveTransaction();
        }
        TransactionBlock tB = new TransactionBlock(t); //Building blocks of tr_count transactions
        for(int i=0;i<t.length;i++){
          if(t[i].Destination.mycoins == null){
            t[i].Destination.mycoins = new ArrayList<Pair<String,TransactionBlock>>();
          }
          t[i].Destination.mycoins.add(new Pair<String, TransactionBlock>(t[i].coinID, tB));  //Adding coins to the mycoinslist of each members
        }
        DSObj.bChain.InsertBlock_Malicious(tB); //Inserting the newly created block in blockchain
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
