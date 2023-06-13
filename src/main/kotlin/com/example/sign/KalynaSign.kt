package com.example.sign

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*


object KalynaDigitalSign {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Security.addProvider(BouncyCastleProvider())

        // Generate a new private-public key pair
        val kpg = KeyPairGenerator.getInstance("EC", "BC")
        kpg.initialize(256, SecureRandom())
        val keyPair = kpg.generateKeyPair()

        // Get the private key
        val privateKey = keyPair.private

        // Create a message to sign
        val message = "This is a message to sign".toByteArray()

        // Sign the message
        val signature = sign(message, privateKey)

        // Verify the signature
        val publicKey = keyPair.public
        val isVerified = verify(message, signature, publicKey)
        println("Signature verification: $isVerified")
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class, SignatureException::class)
    fun sign(message: ByteArray?, privateKey: PrivateKey?): ByteArray {
        val signature = Signature.getInstance("SHA256withECDSA", "BC")
        signature.initSign(privateKey)
        signature.update(message)
        return signature.sign()
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class, SignatureException::class)
    fun verify(message: ByteArray?, signature: ByteArray?, publicKey: PublicKey?): Boolean {
        val sig = Signature.getInstance("SHA256withECDSA", "BC")
        sig.initVerify(publicKey)
        sig.update(message)
        return sig.verify(signature)
    }
}
