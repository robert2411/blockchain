package biz.stevens.blockchain;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Transaction {

    public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey reciepient; // Recipients address/public key.
	public float value;
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	
	public List<TransactionInput> inputs = new ArrayList<>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value,  List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	//Signs all the data we dont wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
		String data = getStringFromKey(sender) + getStringFromKey(reciepient) + Float.toString(value)	;
		signature = applyECDSASig(privateKey,data);
	}
	//Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
		String data = getStringFromKey(sender) + getStringFromKey(reciepient) + Float.toString(value)	;
		return verifyECDSASig(sender, data, signature);
	}

	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return DigestUtils.sha256Hex( getStringFromKey(sender)+ getStringFromKey(reciepient) + Float.toString(value) + sequence);
	}

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	//Verifies a String signature
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	//Returns true if new transaction could be created.
	public boolean processTransaction() {

		if(verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i : inputs) {
			i.setUTXO( Main.UTXOs.get(i.getTransactionOutputId()));
		}

		//check if transaction is valid:
		if(getInputsValue() < Main.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}

		//generate transaction outputs:
		float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
		transactionId = calulateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender

		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			Main.UTXOs.put(o.id , o);
		}

		//remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
			Main.UTXOs.remove(i.getUTXO().id);
		}

		return true;
	}

	//returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
			total += i.getUTXO().value;
		}
		return total;
	}

	//returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}