import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp; // Timestamp in milliseconds
    private int nonce;

    // Block Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;  // Increment nonce
            hash = calculateHash(); // Recalculate hash
        }
        System.out.println("Block Mined!!! : " + hash);
    }
}
