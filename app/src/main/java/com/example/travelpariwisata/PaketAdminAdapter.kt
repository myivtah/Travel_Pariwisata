import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.R
import com.example.travelpariwisata.menu.PaketModel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class PaketAdminAdapter(
    private val context: Context,
    private val paketList: List<PaketModel>
) : RecyclerView.Adapter<PaketAdminAdapter.ViewHolder>() {

    private var onItemClickListener: ((PaketModel) -> Unit)? = null
    private var onEditButtonClickListener: ((PaketModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (PaketModel) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnEditButtonClickListener(listener: (PaketModel) -> Unit) {
        onEditButtonClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageWisataAdmin)
        val judulTextView: TextView = view.findViewById(R.id.textJudulAdmin)
        val hargaTextView: TextView = view.findViewById(R.id.textViewHargaAdmin)
        val hapusButton: MaterialButton = view.findViewById(R.id.materialButtonHapus)
        val editButton: MaterialButton = view.findViewById(R.id.materialButtonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_paket_admin, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.editButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onEditButtonClickListener?.invoke(paketList[position])
            }
        }

        viewHolder.hapusButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Panggil metode showDeleteConfirmationDialog di sini
                showDeleteConfirmationDialog(paketList[position])
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paket = paketList[position]

        Picasso.get().load(paket.imageUrl).into(holder.imageView)
        holder.judulTextView.text = paket.name
        holder.hargaTextView.text = paket.harga.toString()
    }

    override fun getItemCount(): Int {
        return paketList.size
    }

    private fun showDeleteConfirmationDialog(paket: PaketModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus paket ini?")
        builder.setPositiveButton("Ya") { _, _ ->
            onItemClickListener?.invoke(paket)
        }
        builder.setNegativeButton("Tidak", null)
        builder.show()
    }
}