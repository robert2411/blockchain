package biz.stevens.blockchain;

import org.testng.Assert;
import org.testng.annotations.Test;


public class BlockTest {

    @Test
    public void simpleTest() throws InterruptedException {
        Block genesisBlock = new Block("Hi im the first block", "0");
        System.out.println("Hash for block 1 : " + genesisBlock.getHash());

        Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
        System.out.println("Hash for block 2 : " + secondBlock.getHash());

        Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());
        System.out.println("Hash for block 3 : " + thirdBlock.getHash());
        Thread.sleep(1);
        Block thirdBlockCopy = new Block("Hey im the third block", secondBlock.getHash());
        System.out.println("Hash for block 3 copy : " + thirdBlockCopy.getHash());

        Assert.assertFalse(genesisBlock.getHash().equals(secondBlock.getHash()));
        Assert.assertFalse(thirdBlock.getHash().equals(thirdBlockCopy.getHash()));
    }

}