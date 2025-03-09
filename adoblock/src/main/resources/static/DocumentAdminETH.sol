// SPDX-License-Identifier: GPL-3.0
pragma solidity ^0.8.20;

/**
 * @title Contrat intelligent DocumentAdministratifETH
 * @dev Stocke et recherche un document administratif (hash) dans Ethereum
 * VERSION DE CONTRAT DEPLOYE
 */
contract DocumentAdministratifETH {
    struct Document {
        string hashEncoded;
        string signedHashEncoded;
        string publicKeyEncoded;
        uint256 timestamp;
    }

    mapping(string => Document) private documents;

    event DocumentStored(string indexed hashEncoded, uint256 timestamp);

    function storeDocument(
        string memory _hashEncoded,
        string memory _signedHashEncoded,
        string memory _publicKeyEncoded
    ) public {
        require(bytes(documents[_hashEncoded].hashEncoded).length == 0, "Document already exists");

        documents[_hashEncoded] = Document({
            hashEncoded: _hashEncoded,
            signedHashEncoded: _signedHashEncoded,
            publicKeyEncoded: _publicKeyEncoded,
            timestamp: block.timestamp
        });

        emit DocumentStored(_hashEncoded, block.timestamp);
    }

    function getDocument(string memory _hashEncoded)
        public
        view
        returns (string memory, string memory, string memory, uint256)
    {
        require(bytes(documents[_hashEncoded].hashEncoded).length != 0, "Document not found");

        Document memory doc = documents[_hashEncoded];
        return (doc.hashEncoded, doc.signedHashEncoded, doc.publicKeyEncoded, doc.timestamp);
    }
}
