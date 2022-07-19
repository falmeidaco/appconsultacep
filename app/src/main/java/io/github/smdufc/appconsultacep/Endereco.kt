package io.github.smdufc.appconsultacep

class Endereco {
    data class Data(
        val cep : String,
        val logradouro : String,
        val complemento : String,
        val bairro : String,
        val localidade : String,
        val uf : String,
        val ibge : String,
        val gia : String,
        val ddd : String,
        val siafi : String
    )
}