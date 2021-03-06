package `in`.yeng.user.team

import `in`.yeng.user.R
import `in`.yeng.user.helpers.NetworkHelper
import `in`.yeng.user.helpers.viewbinders.BinderSection
import `in`.yeng.user.helpers.viewbinders.BinderTypes
import `in`.yeng.user.home.MainActivity
import `in`.yeng.user.newsupdates.helpers.TeamAdapter
import `in`.yeng.user.team.dom.Team
import `in`.yeng.user.team.network.TeamListAPI
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.satorufujiwara.binder.recycler.RecyclerBinderAdapter

class TeamFragment : Fragment() {

    companion object {
        val TAG: String = this::class.java.simpleName
    }

    private var _context: Context? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        _context = context
    }

    override fun onDetach() {
        super.onDetach()
        _context = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.team_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.isNestedScrollingEnabled = true

        val layoutManager = LinearLayoutManager(_context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = RecyclerBinderAdapter<BinderSection, BinderTypes>()
        recyclerView.adapter = adapter

        NetworkHelper.ifNotConnected(_context) {
            (_context as MainActivity).noConnection.visibility = View.VISIBLE
            Handler().postDelayed({ MainActivity.loadingIndicator.smoothToHide() }, 250)
        }

        NetworkHelper.ifConnected(_context) {
            (_context as MainActivity).noConnection.visibility = View.GONE
        }

        (_context as MainActivity).retry.setOnClickListener {
            TeamListAPI.withListOfTeams { team ->
                bindTeams(team, adapter)
            }
        }

        MainActivity.loadingIndicator.smoothToShow()
        TeamListAPI.withListOfTeams { team ->
            bindTeams(team, adapter)
        }


    }

    fun bindTeams(team: List<Team>, adapter: RecyclerBinderAdapter<BinderSection, BinderTypes>) {

        NetworkHelper.ifNotConnected(_context) {
            (_context as MainActivity).noConnection.visibility = View.VISIBLE
            MainActivity.loadingIndicator.smoothToHide()
        }

        NetworkHelper.ifConnected(_context) {
            (_context as MainActivity).noConnection.visibility = View.GONE
        }

        adapter.clear()


        if (team.isEmpty())
            (_context as MainActivity).noContent.visibility = View.VISIBLE
        else {
            (_context as MainActivity).noContent.visibility = View.GONE
            for (item in team)
                adapter.add(BinderSection.SECTION_1, TeamAdapter(_context as AppCompatActivity, item))
        }
        MainActivity.loadingIndicator.smoothToHide()
    }


}
