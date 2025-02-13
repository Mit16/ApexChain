import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // this is also the hash of the transaction
    public PublicKey sender; // sender address
    public PublicKey recipient; // Recipients address/ public key
    public float value;
    public byte[] signature; // prevent anybody else from spending funds in our wallet

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // a rough count of how many transactions have been generated

    // constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This calculates the transaction hash(which will be used as its Id)
    private String calculateHash() {
        sequence++;// increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }


    //Signs all the data we don't wish to be tampered with
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // verefies the data we signed hasn't been tempered with
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    //Returns true if new transaction could be created
    public boolean processTransaction(){
        if(verifySignature() == false){
            System.out.println("# Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs ( unspent)
        for(TransactionInput i: inputs){
            i.UTXO = ApexChain.UTXOs.get(i.transactionOutputId);
        }

        // check if transaction is valid
        if(getInputsValue()< ApexChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: "+ getInputsValue());
            return false;
        }

        // generate transaction is valid
        float leftOver = getInputValue() - value; // get value of inputs then the left over change
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient,value,transactionId));// send value to recipient
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId)); //send the left over

        // add outputs to Unspent list
        for(TransactionInput i:inputs){
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            ApexChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    // return sum of inputs(UTXOs) values
    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i:inputs){
            if (i.UTXO == null) continue; // if Transactions can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    // return sum of outputs
    public float getOutputsValue(){
        float total =0;
        for(TransactionOutput 0: outputs){
            total += o.value;
        }
        return total;
    }
}
