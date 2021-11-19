package DSCoinPackage;

import HelperClasses.*;
public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    Transaction newarr[] = new Transaction[t.length];
    for(int i = 0; i < t.length; i++) {
      Transaction tr = new Transaction();
      tr.Source = t[i].Source;
      tr.Destination = t[i].Destination;
      tr.coinID = t[i].coinID;
      tr.coinsrc_block = t[i].coinsrc_block;
      newarr[i] = tr;    //Copying all elements of t into newarr, so that if t changes, trarray doesn't change.
    }
    trarray = newarr;
    previous = null;
    
    MerkleTree tree = new MerkleTree();
		tree.Build(t);
		Tree = tree;
    trsummary = tree.rootnode.val;
    dgst = null;
  }

  public boolean checkTransaction (Transaction t) {

    if(t.coinsrc_block == null){ //if coinsrc_block is null, then it is either moderator generated or is reward transaction as mentioned in the project description.
      return true;
    }

    TransactionBlock traverse = this;
    while(traverse != t.coinsrc_block){
      for(int i = 0;i<traverse.trarray.length;i++){
        if(traverse.trarray[i].coinID.equals(t.coinID)){ //Check that no transaction with same coinID should exist in intermediate blocks
          return false;
        }
      }
      traverse = traverse.previous;
    }

    if(traverse == null){ //this will be the case if coinsrc_block doesn't lie on the longestValidChain
      return false;
    }

    for(int i = 0;i<traverse.trarray.length;i++){
      if(traverse.trarray[i].coinID.equals(t.coinID) && traverse.trarray[i].Destination == t.Source){ //Checking that the coin indeed belongs to the buyer. 
        for(int j=i+1; j<traverse.trarray.length;j++){
          if(traverse.trarray[j].coinID.equals(t.coinID) && traverse.trarray[j].Source == t.Source){ //There should be no double spending.
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
}
