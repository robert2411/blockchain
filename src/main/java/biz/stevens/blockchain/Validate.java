package biz.stevens.blockchain;

import java.util.List;

public class Validate {
    private Validate() {
    }

    public static boolean isBlockValid(Block previousBlock, Block block) {
        //compare registered hash and calculated hash:
        if (!block.getHash().equals(block.calculateHash())) {
            System.out.println("Current Hashes not equal");
            return false;
        }
        //compare previous hash and registered previous hash
        if (!previousBlock.getHash().equals(block.getPreviousHash())) {
            System.out.println("Previous Hashes not equal");
            return false;
        }

        return true;
    }

    public static boolean isChainValid(List<Block> chain) {
        Block previous = null;
        for (Block current : chain) {
            if (previous != null && !isBlockValid(previous, current)) {
                return false;
            }
            previous = current;
        }
        return true;
    }
}
