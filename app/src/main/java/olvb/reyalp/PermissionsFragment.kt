package olvb.reyalp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.permissions_fragment.*

class PermissionsFragment : Fragment() {

    private var onPermissionsRequestListener: OnPermissionsRequestListener? = null

    interface OnPermissionsRequestListener {
        fun onPermissionsRequest()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onPermissionsRequestListener = context as? OnPermissionsRequestListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.permissions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grantButton.setOnClickListener {
            onGrantButtonClicked()
        }
    }

    private fun onGrantButtonClicked() {
        onPermissionsRequestListener?.onPermissionsRequest()
    }
}