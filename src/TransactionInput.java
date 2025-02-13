public class TransactionInput {
    public String transactionOutputId; // Refers to TransactionOutputs -> transactionId
    public TransactionOutput UTXO; // Contains the unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
