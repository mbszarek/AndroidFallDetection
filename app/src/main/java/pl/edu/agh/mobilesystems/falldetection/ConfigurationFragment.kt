package pl.edu.agh.mobilesystems.falldetection

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import pl.edu.agh.mobilesystems.falldetection.utils.Constants


class ConfigurationFragment : Fragment(R.layout.fragment_configuration), TextWatcher {
    private lateinit var textField: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuration, container, false)

        textField = view!!.findViewById(R.id.editTextPhone)
        saveButton = view.findViewById(R.id.saveButton)

        val sharedPreferences = activity!!.applicationContext.getSharedPreferences(
            Constants.AppName,
            Context.MODE_PRIVATE
        )
        val iceNumber = sharedPreferences.getString(Constants.IceNumberField, null)

        textField.addTextChangedListener(this)

        iceNumber?.also {
            textField.setText(it)
        }

        saveButton.setOnClickListener {
            val phoneNumber = textField.text.trim().toString()

            with(sharedPreferences.edit()) {
                putString(Constants.IceNumberField, phoneNumber)
                apply()
            }

            Toast.makeText(view.context, "Phone number saved", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        saveButton.isEnabled =
            s != null && s.trim() != "" && s.length <= 10 && Patterns.PHONE.matcher(s).matches()
    }
}