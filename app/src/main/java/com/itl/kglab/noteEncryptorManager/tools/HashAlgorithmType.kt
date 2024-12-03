package com.itl.kglab.noteEncryptorManager.tools

sealed class HashAlgorithmType(
    val algorithmName: String
) {

    data object Sha256 : HashAlgorithmType("SHA-256")
    data object Sha512 : HashAlgorithmType("SHA-512")

    companion object {
        fun getList(): List<HashAlgorithmType> {
            return listOf(
                Sha256,
                Sha512
            )
        }

        fun getTypeByName(name: String): HashAlgorithmType {
            val list = getList()

            return list.find { it.algorithmName == name } ?: Sha256
        }
    }
}