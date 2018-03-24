package biz.stevens.blockchain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TransactionInput {
    private String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    @Setter
    private TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
