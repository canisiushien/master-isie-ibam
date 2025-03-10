package bf.canisiuslab.adoblock.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.gas.StaticGasProvider;
import java.math.BigInteger;

import bf.canisiuslab.adoblock.service.EthereumService;
import bf.canisiuslab.adoblock.service.dto.DocumentETH;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation de la logique pour interagir avec la blockchain Ethereum via
 * Web3j
 * 
 * @author Canisius <canisiushien@gmail.com>
 */
@Slf4j
@Service
public class EthereumServiceImpl implements EthereumService {
    /** Clé privée du compte Ethereum fourni par Ganache */
    @Value("${ethereum.ganache.private-key}")
    private String privateKey;

    /** Adresse du contrat intelligent (DocumentRegistry) déployé */
    @Value("${ethereum.smartcontract.address}")
    private String contratAddress;

    /** URL RPC de Ganache */
    @Value("${ethereum.ganache.rpc-url}")
    private String rpcUrl;

    /** 2 Gwei = frais de gas */
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(2_000_000_000L);

    /** Limite de gas */
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_700_000);

    /** definitions des connecteurs (api, cle privee, contrat intelligent) */
    private final Web3j web3j;

    private final Credentials credentials;

    private final DocumentETH contract;

    /**
     * Constructeur : initialise la connexion à Ethereum et charge le smart contract
     */
    public EthereumServiceImpl() {
        /** Connexion au réseau Ethereum (par Ganache en local) */
        this.web3j = Web3j.build(new HttpService(rpcUrl));

        /** Chargement des informations d'identification (clé privée) */
        this.credentials = Credentials.create(privateKey);

        /** Chargement du contrat intelligent déjà déployé sur Ethereum */
        this.contract = DocumentETH.load(contratAddress, web3j, credentials,
                new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
    }

    /**
     * Stocke un document administratif (le hash précisement) sur la blockchain
     * Ethereum
     *
     * @param hashEncoded       Hachage du document (SHA-256)
     * @param signedHashEncoded Signature du hachage avec la clé privée
     * @param publicKeyEncoded  Clé publique associée
     * @return Hash de la transaction
     * @throws Exception en cas d'erreur d'exécution
     */
    @Override
    public String storeDocument(String hashEncoded, String signedHashEncoded, String publicKeyEncoded)
            throws Exception {
        log.info("Appel de la fonction storeDocument du smart contract");
        // Appel de la fonction storeDocument du smart contract
        RemoteFunctionCall<TransactionReceipt> transaction = contract.storeAdministrativeDocument(hashEncoded,
                signedHashEncoded,
                publicKeyEncoded);
        // Envoie de la transaction et récupération du reçu
        TransactionReceipt receipt = transaction.send();

        // Retourne l'ID de la transaction blockchain
        return receipt.getTransactionHash();
    }

    /**
     * Récupère un document administratif stocké dans la blockchain Ethereum à
     * partir de son hash.
     *
     * @param hashEncoded Hachage du document
     * @return Un objet Document contenant les détails stockés
     * @throws Exception en cas d'erreur d'exécution
     */
    @Override
    public DocumentETH getDocument(String hashEncoded) throws Exception {
        log.info("Appel de la fonction getDocument du smart contract");
        // Appel de la fonction getDocument du smart contract
        Tuple4<String, String, String, BigInteger> result = contract.getAdministrativeDocument(hashEncoded).send();

        // Retourne un objet contenant les informations récupérées
        return new DocumentETH(result.component1(), result.component2(), result.component3(),
                result.component4().longValue());
    }

}