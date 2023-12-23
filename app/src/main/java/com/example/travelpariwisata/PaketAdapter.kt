import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.DetailFragment
import com.example.travelpariwisata.R
import com.example.travelpariwisata.menu.PaketModel
import com.squareup.picasso.Picasso

class PaketAdapter(private val context: Context, private val paketList: List<PaketModel>) :
    RecyclerView.Adapter<PaketAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageWisata)
        val judulTextView: TextView = view.findViewById(R.id.textViewJudul)
        val hargaTextView: TextView = view.findViewById(R.id.textViewHarga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_paket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paket = paketList[position]

        Picasso.get().load(paket.imageUrl).into(holder.imageView)
        holder.judulTextView.text = paket.name
        holder.hargaTextView.text = paket.harga.toString()

        holder.itemView.setOnClickListener {
            navigateToDetailFragment(paket)
        }
    }

    private fun navigateToDetailFragment(paket: PaketModel) {
        if (context is FragmentActivity) {
            val fragmentManager = context.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val detailFragment = DetailFragment.newInstance(paket)

            fragmentTransaction.replace(R.id.fragment_container, detailFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}
