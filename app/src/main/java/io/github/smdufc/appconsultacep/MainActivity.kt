package io.github.smdufc.appconsultacep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    /* Inicializa widgets */
    val buttonConsult: Button = findViewById(R.id.button_consult)
    val textViewResult: TextView = findViewById(R.id.textview_result)
    val inputCEP: EditText = findViewById(R.id.input_cep)

    /* Ação de consultar CEP */
    buttonConsult.setOnClickListener {
      val cep = inputCEP.text.toString()
      val url = "https://viacep.com.br/ws/$cep/json/"
      val queue = Volley.newRequestQueue(this)
      val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
        { response ->
          textViewResult.text = response.toString()
        },
        { error ->
          textViewResult.text = error.message
        })
      queue.add(jsonObjectRequest)
    }

  }

}