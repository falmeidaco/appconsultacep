package io.github.smdufc.appconsultacep

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

  private lateinit var buttonConsult: Button
  private lateinit var textViewResult: TextView
  private lateinit var inputCEP: EditText
  private lateinit var uiProgressBar: ProgressBar
  private lateinit var uiIconSuccess: ImageView
  private lateinit var uiIconError: ImageView
  private lateinit var uiIconNotFound: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Inicializa widgets
    buttonConsult = findViewById(R.id.button_consult)
    textViewResult = findViewById(R.id.textview_result)
    inputCEP = findViewById(R.id.input_cep)
    uiProgressBar = findViewById(R.id.ui_progressbar)
    uiIconSuccess = findViewById(R.id.ui_icon_success)
    uiIconError = findViewById(R.id.ui_icon_error)
    uiIconNotFound = findViewById(R.id.ui_icon_notfound)

    // Ação de consultar CEP  - botão */
    buttonConsult.setOnClickListener {
      // Verifica se o campo de CEP está vazio */
      if (inputCEP.text.toString().isNotEmpty()) {
        textViewResult.text = ""
        hideKeyboard()
        consultCEP()
      } else {
        Toast.makeText(this, R.string.error_empty, Toast.LENGTH_LONG).show()
      }
    }

    // Ação de consultar CEP  - ação do teclado virtual
    inputCEP.setOnEditorActionListener {_, actionId, event ->
      // Consulta clicar no botão de ação do teclado ou ao pressionar a tecla enter */
      if(actionId == EditorInfo.IME_ACTION_SEARCH
        || actionId == EditorInfo.IME_ACTION_DONE
        || event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
        consultCEP()
      }
      false
    }
  }

  /* Metodo para consultar CEP */
  private fun consultCEP() {
    // Esconde icones de feedback da consulta
    uiIconSuccess.visibility = ImageView.INVISIBLE
    uiIconError.visibility = ImageView.INVISIBLE
    uiIconNotFound.visibility = ImageView.INVISIBLE
    // Define UI com estado de processo em execução
    setUIState(false)
    val cep = inputCEP.text.toString()
    val url = "https://viacep.com.br/ws/$cep/json/"
    val queue = Volley.newRequestQueue(this)
    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
      { response ->
        // Quando a requisição retorna sucesso mas o CEP não é encontrado
        if (response.has("erro")) {
          Toast.makeText(this, R.string.error_not_found, Toast.LENGTH_LONG).show()
          uiIconNotFound.visibility = ImageView.VISIBLE
        // Quando a requissição retorna sucesso e o CEP é encontrado
        } else {
          val cep = response.getString("cep")
          val logradouro = response.getString("logradouro")
          val bairro = response.getString("bairro")
          val localidade = response.getString("localidade")
          val uf = response.getString("uf")
          val ibge = response.getString("ibge")
          val gia = response.getString("gia")
          val ddd = response.getString("ddd")
          val siafi = response.getString("siafi")
          uiIconSuccess.visibility = ImageView.VISIBLE
          textViewResult.text =
            "CEP: $cep\nLogradouro: $logradouro\nBairro: $bairro\nLocalidade: $localidade\nUF: $uf\nIBGE: $ibge\nGIA: $gia\nDDD: $ddd\nSIAF: $siafi"
        }
        // Define UI com estado de processo concluído
        setUIState(true)
      },
      { error ->
        Log.e("APPCONSULTACEP_ERROR", error.message.toString())
        Toast.makeText(this, R.string.error_request, Toast.LENGTH_LONG).show()
        uiIconError.visibility = ImageView.VISIBLE
        setUIState(true)
      })
    queue.add(jsonObjectRequest)
  }

  // Controla o estado da UI definindo processo em execução ou concluído
  fun setUIState(state: Boolean) {
    buttonConsult.isEnabled = state
    if (state) {
      uiProgressBar.visibility = ProgressBar.INVISIBLE
    } else {
      uiProgressBar.visibility = ProgressBar.VISIBLE
    }
  }

  // Métodos para ocultar o teclado virtual programaticamente
  fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
  }

  fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
  }

  fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
  }

}