package io.github.smdufc.appconsultacep

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    /* Inicializa widgets */
    buttonConsult = findViewById(R.id.button_consult)
    textViewResult = findViewById(R.id.textview_result)
    inputCEP = findViewById(R.id.input_cep)
    uiProgressBar = findViewById(R.id.ui_progressbar)
    uiIconSuccess = findViewById(R.id.ui_icon_success)

    /* Ação de consultar CEP  - botão */
    buttonConsult.setOnClickListener {
      if (inputCEP.text.toString().isNotEmpty()) {
        consultCEP()
      } else {
        Toast.makeText(this, R.string.error_empty, Toast.LENGTH_LONG).show()
      }
    }

    /* Ação de consultar CEP  - ação do teclado virtual */
    inputCEP.setOnEditorActionListener {_, actionId, _ ->
      if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
        consultCEP()
      }
      false
    }

  }

  private fun consultCEP() {
    uiIconSuccess.visibility = ImageView.INVISIBLE
    setUIState(false)
    val cep = inputCEP.text.toString()
    val url = "https://viacep.com.br/ws/$cep/json/"
    val queue = Volley.newRequestQueue(this)
    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
      { response ->
        if (response.has("erro")) {
          textViewResult.text = R.string.error_not_found.toString()
        } else {
          val logradouro = response.getString("logradouro")
          val bairro = response.getString("bairro")
          val localidade = response.getString("localidade")
          val uf = response.getString("uf")
          uiIconSuccess.visibility = ImageView.VISIBLE
          hideKeyboard()
          textViewResult.text = "$logradouro, $bairro - $localidade - $uf"
        }
        setUIState(true)
      },
      { error ->
        Log.e("APPCONSULTACEP_ERROR", error.message.toString())
        Toast.makeText(this, R.string.error_request, Toast.LENGTH_LONG).show()
        setUIState(true)
      })
    queue.add(jsonObjectRequest)
  }

  fun setUIState(state: Boolean) {
    buttonConsult.isEnabled = state
    if (state) {
      uiProgressBar.visibility = ProgressBar.INVISIBLE
    } else {
      uiProgressBar.visibility = ProgressBar.VISIBLE
    }
  }

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