package com.carrent.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class IpfsService {
    private final IPFS ipfs;

    public IpfsService(@Value("${ipfs.node.url}") String ipfsUrl) {
        String[] hostAndPort = ipfsUrl.replace("http://", "").split(":");
        this.ipfs = new IPFS(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(file.getInputStream());
            MerkleNode response = ipfs.add(is).get(0);
            return response.hash.toString();
        } catch (IOException e) {
            log.error("Error uploading file to IPFS", e);
            throw e;
        }
    }

    public String uploadJson(String jsonContent) throws IOException {
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(inputStream);
            MerkleNode response = ipfs.add(is).get(0);
            return response.hash.toString();
        } catch (IOException e) {
            log.error("Error uploading JSON to IPFS", e);
            throw e;
        }
    }

    public byte[] downloadFile(String hash) throws IOException {
        try {
            Multihash filePointer = Multihash.fromBase58(hash);
            return ipfs.cat(filePointer);
        } catch (IOException e) {
            log.error("Error downloading file from IPFS", e);
            throw e;
        }
    }
}
