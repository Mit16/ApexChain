import java.util.Date;
import java.util.ArrayList;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // our data will be a simple message
    private long timeStamp; // Timestamp in milliseconds
    private int nonce;

    // Block Constructor
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    // Calculate new hash based on blocks contents
    public String calculateHash() {
        return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');  // Create a string with difficulty "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;  // Increment nonce
            hash = calculateHash(); // Recalculate hash
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    // Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) return false;
        if (previousHash.equals("0")) {  // Use `.equals()` for string comparison
            return true;  // Genesis block doesn't process transactions
        }

        if (!transaction.processTransaction()) {
            System.out.println("Transaction failed to process. Discarded.");
            return false;
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

}
