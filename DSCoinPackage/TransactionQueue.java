package DSCoinPackage;
import HelperClasses.*;
public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(firstTransaction == null) { //if firstTransaction is null
      numTransactions = 1;
      firstTransaction = transaction;
      lastTransaction = transaction;
      return;
    }
    //If firstTransaction is not null
    lastTransaction.next = transaction; //Enqueue a transaction
    lastTransaction = transaction; //adjust lastTransaction pointer
    numTransactions++; //increase numTransactions by 1
    return;

  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    try {
      if(firstTransaction == null) {
        throw new EmptyQueueException(); //if Queue is empty
      }
      Transaction transaction = firstTransaction; //dequeue 1st transaction
      firstTransaction = firstTransaction.next; //adjust firstTransaction pointer
      numTransactions--; //decrease numTransactions by 1
      return transaction;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public int size() {
    return numTransactions;
  }
}
