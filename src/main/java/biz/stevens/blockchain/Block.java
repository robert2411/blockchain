package biz.stevens.blockchain;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Block {
    private final String previousHash;
    private String merkleRoot;
    private final long timeStamp;
    private List<Transaction> transactions = new ArrayList<>(); //our data will be a simple message.
    private String hash;
    private long nonce = 0;

    public Block(@NonNull String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public static String calculateHash(String previousHash, String merkleRoot, long timeStamp, long nonce) {
        return DigestUtils.sha256Hex(previousHash + Long.toString(timeStamp) + Long.toString(nonce) + merkleRoot);
    }

    public String calculateHash() {
        return calculateHash(previousHash, merkleRoot, timeStamp, nonce);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);

    }

    //Tacks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        List<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(DigestUtils.sha256Hex(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((!"0".equals(previousHash))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
