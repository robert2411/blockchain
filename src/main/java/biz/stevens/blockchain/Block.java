package biz.stevens.blockchain;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@ToString
public class Block {
    private final String previousHash;
    private final String data;
    private final long timeStamp;
    private String hash;
    private long nonce = 0;

    public Block(@NonNull String data, @NonNull String previousHash) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public static String calculateHash(String previousHash, String data, long timeStamp, long nonce) {
        return DigestUtils.sha256Hex(previousHash + Long.toString(timeStamp) + Long.toString(nonce) + data);
    }

    public String calculateHash() {
        return calculateHash(previousHash, data, timeStamp, nonce);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);

    }
}
